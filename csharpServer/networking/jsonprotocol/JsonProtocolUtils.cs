using System;
using System.Collections.Generic;
using model;
using networking.dto;

namespace networking.jsonprotocol;



public static class JsonProtocolUtils
{
    public static Response CreateEmployeeLoggedInResponse(Employee employee)
    {
        return new Response { ResponseType = ResponseType.EMPLOYEE_LOGGED_IN, LoggedEmployee = employee };
    }

    public static Response CreateOkResponse()
    {
        return new Response { ResponseType = ResponseType.OK };
    }

    public static Response CreateErrorResponse(string errorMessage)
    {
        return new Response { ResponseType = ResponseType.ERROR, ErrorMessage = errorMessage };
    }

    public static Response CreateSeatsReservedResponse()
    {
        return new Response { ResponseType = ResponseType.SEATS_RESERVED };
    }

    public static Response CreateFindAllTripsResponse(List<Trip> trips)
    {
        return new Response { ResponseType = ResponseType.FIND_ALL_TRIPS, Trips = trips };
    }

    public static Response CreateSearchTripSeatsResponse(List<SeatDTO> seats)
    {
        return new Response { ResponseType = ResponseType.FIND_TRIP_SEATS, Seats = seats };
    }

    public static Response CreateGetTripResponse(Trip trip)
    {
        return new Response { ResponseType = ResponseType.FIND_TRIP, Trip = trip };
    }

    public static Request CreateLoginRequest(Employee employee)
    {
        return new Request { RequestType = RequestType.LOGIN, EmployeeDTO = DTOUtils.GetEmployeeDTO(employee) };
    }

    public static Request CreateLoginRequest(string username, string password)
    {
        return new Request { RequestType = RequestType.LOGIN, Username = username, Password = password };
    }

    public static Request CreateGetAllTripsRequest()
    {
        return new Request { RequestType = RequestType.GET_ALL_TRIPS };
    }

    public static Request CreateSearchTripSeatsRequest(string destination, DateOnly tripDate, TimeOnly tripTime)
    {
        return new Request
        {
            RequestType = RequestType.SEARCH_TRIP_SEATS,
            TripDestination = destination,
            TripDate = tripDate,
            TripTime = tripTime
        };
    }

    public static Request CreateReserveSeatsRequest(string clientName, List<int> seatNumbers, Trip trip, Employee employee)
    {
        return new Request
        {
            RequestType = RequestType.RESERVE_SEATS,
            ClientName = clientName,
            SeatsNumbers = seatNumbers,
            TripToReserve = trip,
            CurentEmployee = employee
        };
    }

    public static Request CreateGetTripRequest(string destination, DateOnly tripDate, TimeOnly tripTime)
    {
        return new Request
        {
            RequestType = RequestType.GET_TRIP,
            Destination = destination,
            TripDate = tripDate,
            TripTime = tripTime
        };
    }

    public static Request CreateLogoutRequest(Employee employee)
    {
        return new Request { RequestType = RequestType.LOGOUT, CurentEmployee = employee };
    }
}