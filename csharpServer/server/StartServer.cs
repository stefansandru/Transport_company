using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Net.Sockets;
using System.Reflection;
using System.Threading;
using log4net;
using log4net.Config;
using networking;
using networking.jsonprotocol;
using persistance;
using server;
using services;

namespace server
{
    public class StartServer
    {
        private static int DEFAULT_PORT = 55556;
        private static string DEFAULT_IP = "127.0.0.1";
        private static readonly ILog log = LogManager.GetLogger(typeof(StartServer));

        public static void Main(string[] args)
        {
            // Configurare log4net
            var logRepository = LogManager.GetRepository(Assembly.GetEntryAssembly());
            XmlConfigurator.Configure(logRepository, new FileInfo("log4net.config"));

            log.Info("Starting task management server");
            log.Info("Reading properties from app.config ...");

            int port = DEFAULT_PORT;
            string ip = DEFAULT_IP;

            string portS = ConfigurationManager.AppSettings["port"];
            if (portS == null)
            {
                log.Debug("Port property not set. Using default value " + DEFAULT_PORT);
            }
            else
            {
                bool result = int.TryParse(portS, out port);
                if (!result)
                {
                    log.Debug("Port property not a number. Using default value " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            }

            string ipS = ConfigurationManager.AppSettings["ip"];
            if (ipS != null)
            {
                ip = ipS;
            }
            else
            {
                log.Info("IP property not set. Using default value " + DEFAULT_IP);
            }

            
            log.InfoFormat("Configuration Settings for database {0}", GetConnectionStringByName("taskDB"));
            IDictionary<string, string> props = new SortedList<string, string>();
            props.Add("ConnectionString", GetConnectionStringByName("taskDB"));
            

            // Instanțiere repository-uri - adaptează la necesar
            IOfficeRepository officeRepository = new OfficeRepository();
            IClientRepository clientRepo = new ClientRepository();
            IEmployeeRepository employeeRepo = new EmployeeRepository(officeRepository);
            IDestinationRepository destinationRepository = new DestinationRepository();
            ITripRepository tripRepo = new TripRepository(destinationRepository);
            IReservedSeatRepository reservedSeatRepo = new ReservedSeatRepository(tripRepo, employeeRepo, clientRepo);

            ITaskManagementServices serviceImpl = new TaskManagementSystemServicesImpl(
                clientRepo, employeeRepo, reservedSeatRepo, tripRepo);
            
            
            try
            {
                Console.WriteLine("Test: trips from database:");
                var trips = serviceImpl.GetAllTrips();
                foreach (var trip in trips)
                {
                    Console.WriteLine($"{trip.Id}: {trip.Destination.Name}");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Eroare la citirea trips: " + ex.Message);
            }

            
            log.DebugFormat("Starting server on IP {0} and port {1}", ip, port);
            
            TaskManagementJsonServer server = new TaskManagementJsonServer(ip, port, serviceImpl);
            server.Start();
            log.Debug("Server started ...");
            Console.WriteLine("Press <enter> to exit...");
            Console.ReadLine();
        }

        static string GetConnectionStringByName(string name)
        {
            string returnValue = null;
            ConnectionStringSettings settings = ConfigurationManager.ConnectionStrings[name];
            if (settings != null)
                returnValue = settings.ConnectionString;
            return returnValue;
        }
    }

    // Serverul JSON concret pentru TaskManagement
    public class TaskManagementJsonServer : ConcurrentServer
    {
        private readonly ITaskManagementServices server;
        private static readonly ILog log = LogManager.GetLogger(typeof(TaskManagementJsonServer));

        public TaskManagementJsonServer(string host, int port, ITaskManagementServices server) : base(host, port)
        {
            this.server = server;
            log.Debug("Creating TaskManagementJsonServer...");
        }

        protected override Thread createWorker(TcpClient client)
        {
            TaskManagementClientJsonWorker worker = new TaskManagementClientJsonWorker(server, client);
            return new Thread(worker.run);
        }
    }
}