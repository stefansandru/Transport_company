
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using model;
using services;
using log4net;

namespace networking.jsonprotocol
{
    public class TaskManagementServicesJsonProxy : ITaskManagementServices
    {
        private string host;
        private int port;
        private IObserver client;
        private NetworkStream stream;
        private TcpClient connection;
        private Queue<Response> responses;
        private volatile bool finished;
        private EventWaitHandle _waitHandle;
        private static readonly ILog log = LogManager.GetLogger(typeof(TaskManagementServicesJsonProxy));

        public TaskManagementServicesJsonProxy(string host, int port)
        {
            this.host = host;
            this.port = port;
            this.responses = new Queue<Response>();
        }

        public Employee Login(string username, string password, IObserver client)
        {
            InitializeConnection();
            var request = JsonProtocolUtils.CreateLoginRequest(username, password);
            SendRequest(request);
            var response = ReadResponse();
            if (response.ResponseType == ResponseType.EMPLOYEE_LOGGED_IN)
            {
                this.client = client;
                // return DTOUtils.GetEmployee(response.LoggedEmployee);
                if (response.LoggedEmployee == null)
                    throw new ServicesException("Server response invalid: No employee data in successful login.");
                return response.LoggedEmployee;
            }
            if (response.ResponseType == ResponseType.ERROR)
            {
                string err = response.ErrorMessage;
                CloseConnection();
                throw new ServicesException(err);
            }
            return null;
        }

        public void Logout(Employee employee)
        {
            SendRequest(JsonProtocolUtils.CreateLogoutRequest(employee));
            Response response = ReadResponse();
            CloseConnection();
            if (response.ResponseType == ResponseType.ERROR)
            {
                throw new ServicesException(response.ErrorMessage);
            }
        }

        public List<Trip> GetAllTrips()
        {
            SendRequest(JsonProtocolUtils.CreateGetAllTripsRequest());
            Response response = ReadResponse();
            if (response.ResponseType == ResponseType.ERROR)
            {
                throw new ServicesException(response.ErrorMessage);
            }
            if (response.ResponseType == ResponseType.FIND_ALL_TRIPS)
            {
                return response.Trips;
            }
            return response.Trips;
        }

        public List<SeatDTO> SearchTripSeats(string destination, DateOnly date, TimeOnly time)
        {
            SendRequest(JsonProtocolUtils.CreateSearchTripSeatsRequest(destination, date, time));
            Response response = ReadResponse();
            if (response.ResponseType == ResponseType.ERROR)
            {
                throw new ServicesException(response.ErrorMessage);
            }
            return response.Seats;
        }

        public void ReserveSeats(string clientName, List<int> seatNumbers, Trip trip, Employee employee)
        {
            SendRequest(JsonProtocolUtils.CreateReserveSeatsRequest(clientName, seatNumbers, trip, employee));
            Response response = ReadResponse();
            if (response.ResponseType == ResponseType.ERROR)
            {
                throw new ServicesException(response.ErrorMessage);
            }
        }

        public Trip GetTrip(string destination, DateOnly date, TimeOnly time)
        {
            SendRequest(JsonProtocolUtils.CreateGetTripRequest(destination, date, time));
            Response response = ReadResponse();
            if (response.ResponseType == ResponseType.ERROR)
            {
                throw new ServicesException(response.ErrorMessage);
            }
            return response.Trip;
        }

        private void InitializeConnection()
        {
            try
            {
                connection = new TcpClient(host, port);
                stream = connection.GetStream();
                finished = false;
                _waitHandle = new AutoResetEvent(false);
                StartReader();
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }

        private void CloseConnection()
        {
            finished = true;
            try
            {
                stream?.Close();
                connection?.Close();
                _waitHandle?.Close();
                client = null;
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
        }

        private void SendRequest(Request request)
        {
            try
            {
                lock (stream)
                {
                    string jsonRequest = JsonSerializer.Serialize(request);
                    log.DebugFormat("Sending request {0}", jsonRequest);
                    byte[] data = Encoding.UTF8.GetBytes(jsonRequest + "\n");
                    stream.Write(data, 0, data.Length);
                    stream.Flush();
                }
            }
            catch (Exception e)
            {
                throw new ServicesException("Error sending object " + e);
            }
        }

        private Response ReadResponse()
        {
            Response response = null;
            try
            {
                _waitHandle.WaitOne();
                lock (responses)
                {
                    response = responses.Dequeue();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }
            return response;
        }

        private void StartReader()
        {
            Thread tw = new Thread(Run);
            tw.Start();
        }

        private void HandleUpdate(Response response)
        {
            log.DebugFormat("HandleUpdate called with {0}", response);
            
            if (response.ResponseType == ResponseType.SEATS_RESERVED)
            {
                try
                {
                    client.SeatsReserved();
                }
                catch (ServicesException e)
                {
                    log.Error(e.StackTrace);
                }
            }
        }

        private bool IsUpdate(Response response)
        {
            return response.ResponseType == ResponseType.SEATS_RESERVED;
        }

        public void Run()
        {
            using var reader = new System.IO.StreamReader(stream, Encoding.UTF8);
            while (!finished)
            {
                try
                {
                    var responseJson = reader.ReadLine();
                    if (string.IsNullOrEmpty(responseJson))
                        continue;
                    Response response = JsonSerializer.Deserialize<Response>(responseJson);
                    log.Debug("Response received " + response);
                    if (IsUpdate(response))
                    {
                        HandleUpdate(response);
                    }
                    else
                    {
                        lock (responses)
                        {
                            responses.Enqueue(response);
                        }
                        _waitHandle.Set();
                    }
                }
                catch (Exception e)
                {
                    log.Error("Reading error " + e);
                }
            }
        }
    }
}