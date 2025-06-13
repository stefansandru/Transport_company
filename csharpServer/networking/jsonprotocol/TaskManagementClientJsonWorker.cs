
using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Text.Json;
using System.Threading;
using model;
using services;
using log4net;

namespace networking.jsonprotocol
{
    public class TaskManagementClientJsonWorker : IObserver
    {
        private ITaskManagementServices server;
        private TcpClient connection;
        private NetworkStream stream;
        private volatile bool connected;
        private static readonly ILog log = LogManager.GetLogger(typeof(TaskManagementClientJsonWorker));

        public TaskManagementClientJsonWorker(ITaskManagementServices server, TcpClient connection)
        {
            this.server = server;
            this.connection = connection;
            try
            {
                stream = connection.GetStream();
                connected = true;
            }
            catch (Exception e)
            {
                log.Error(e.StackTrace);
            }
        }

        public virtual void run()
        {
            using var reader = new System.IO.StreamReader(stream, Encoding.UTF8);
            while (connected)
            {
                try
                {
                    string requestJson = reader.ReadLine();
                    if (string.IsNullOrEmpty(requestJson)) continue;
                    log.DebugFormat("Received JSON request {0}", requestJson);
                    Request request = JsonSerializer.Deserialize<Request>(requestJson);
                    log.DebugFormat("Deserialized Request {0}", request);

                    Response response = handleRequest(request);
                    if (response != null)
                    {
                        sendResponse(response);
                    }
                }
                catch (Exception e)
                {
                    log.ErrorFormat("run error {0}", e.Message);
                    if (e.InnerException != null)
                        log.ErrorFormat("run inner error {0}", e.InnerException.Message);
                    log.Error(e.StackTrace);
                }

                try
                {
                    Thread.Sleep(1000);
                }
                catch (Exception e)
                {
                    log.Error(e.StackTrace);
                }
            }
            try
            {
                stream.Close();
                connection.Close();
            }
            catch (Exception e)
            {
                log.Error("Error " + e);
            }
        }

        public virtual void SeatsReserved()
        {
            log.Debug("Seats reserved update sent");
            try
            {
                sendResponse(JsonProtocolUtils.CreateSeatsReservedResponse());
            }
            catch (Exception e)
            {
                log.Error("Sending update error: " + e);
            }
        }

        public virtual void EmployeeLoggedIn(Employee employee)
        {
            log.DebugFormat("Employee logged in {0}", employee);
            try
            {
                sendResponse(JsonProtocolUtils.CreateEmployeeLoggedInResponse(employee));
            }
            catch (Exception e)
            {
                log.Error(e.StackTrace);
            }
        }

        private static Response okResponse = JsonProtocolUtils.CreateOkResponse();

        private Response handleRequest(Request request)
        {
            Response response = null;
            if (request.RequestType == RequestType.LOGIN)
            {
                log.Debug("Login request ...");
                // Employee emp = DTOUtils.GetEmployeeFromDTO(request.Employee);
                if (request.Username == null || request.Password == null)
                {
                    return JsonProtocolUtils.CreateErrorResponse("Username or password is null");
                }
                var username = request.Username;
                var password = request.Password;
                try
                {
                    lock (server)
                    {
                        var employee = server.Login(username, password, this);
                        return JsonProtocolUtils.CreateEmployeeLoggedInResponse(employee);
                    }
                }
                catch (ServicesException e)
                {
                    connected = false;
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            if (request.RequestType == RequestType.LOGOUT)
            {
                log.Debug("Logout request");
                if (request.CurentEmployee == null)
                {
                    log.Debug("CurentEmployee is null");
                    return JsonProtocolUtils.CreateErrorResponse("CurentEmployee is null");
                }
                Employee emp = request.CurentEmployee;
                try
                {
                    lock (server)
                    {
                        server.Logout(emp);
                    }
                    connected = false;
                    return okResponse;
                }
                catch (ServicesException e)
                {
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            
            if (request.RequestType == RequestType.GET_ALL_TRIPS)
            {
                log.Debug("GetAllTrips request ...");
                try
                {
                    List<Trip> trips;
                    lock (server)
                    {
                        trips = server.GetAllTrips();
                    }
                    return JsonProtocolUtils.CreateFindAllTripsResponse(trips);
                }
                catch (ServicesException e)
                {
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            
            if (request.RequestType == RequestType.SEARCH_TRIP_SEATS)
            {
                log.Debug("SearchTripSeats request ...");
                try
                {
                    List<SeatDTO> seats;
                    lock (server)
                    {
                        seats = server.SearchTripSeats(request.TripDestination, request.TripDate, request.TripTime);
                    }
                    return JsonProtocolUtils.CreateSearchTripSeatsResponse(seats);
                }
                catch (ServicesException e)
                {
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            if (request.RequestType == RequestType.RESERVE_SEATS)
            {
                log.Debug("ReserveSeats request ...");
                try
                {
                    lock (server)
                    {
                        server.ReserveSeats(request.ClientName, request.SeatsNumbers, request.TripToReserve, request.CurentEmployee);
                    }
                    return okResponse;
                }
                catch (ServicesException e)
                {
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            if (request.RequestType == RequestType.GET_TRIP)
            {
                log.Debug("GetTrip request ...");
                try
                {
                    Trip trip;
                    lock (server)
                    {
                        trip = server.GetTrip(request.Destination, request.TripDate, request.TripTime);
                    }
                    return JsonProtocolUtils.CreateGetTripResponse(trip);
                }
                catch (ServicesException e)
                {
                    return JsonProtocolUtils.CreateErrorResponse(e.Message);
                }
            }
            return response;
        }

        private void sendResponse(Response response)
        {
            string jsonString = JsonSerializer.Serialize(response);
            log.DebugFormat("Sending response {0}", jsonString);
            lock (stream)
            {
                byte[] data = Encoding.UTF8.GetBytes(jsonString + "\n");
                stream.Write(data, 0, data.Length);
                stream.Flush();
            }
        }
    }
}