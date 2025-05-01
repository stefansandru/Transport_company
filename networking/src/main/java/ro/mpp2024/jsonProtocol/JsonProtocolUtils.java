package ro.mpp2024.jsonProtocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.Employee;
import ro.mpp2024.Office;
import ro.mpp2024.SeatDTO;
import ro.mpp2024.Trip;
import ro.mpp2024.dto.DTOUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Stack;

public class JsonProtocolUtils {
    private static final Logger logger = LogManager.getLogger(JsonProtocolUtils.class);

    public static Request createLoginRequest(Employee employee) {
        logger.trace("Entering createLoginRequest(Employee employee)");
        Request request = new Request();
        request.setRequestType(RequestType.LOGIN);
        request.setEmployeeDTO(DTOUtils.getEmployeeDTO(employee));
        return request;
    }

    public static Request createLoginRequest(String username, String password) {
        logger.trace("Entering createLoginRequest(String username, String password)");
        Request request = new Request();
        Employee employee = new Employee(null, username, password, null);
        request.setRequestType(RequestType.LOGIN);
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    public static Response createEmployeeLoggedInResponse(Employee employee) {
        logger.trace("Entering createEmployeeLoggedInResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.EMPLOYEE_LOGGED_IN);
//        response.setLoggedInEmployeeDTO(DTOUtils.getEmployeeDTO(employee));
        response.setLoggedEmployee(employee);
        return response;
    }

    public static Response createOkResponse() {
        logger.trace("Entering createOkResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.OK);
        return response;
    }

    public static Response createErrorResponse(String message) {
        logger.trace("Entering createErrorResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.ERROR);
        response.setErrorMessage(message);
        return response;
    }

    public static Response createSeatsReservedResponse() {
        logger.trace("Entering createSeatsReservedResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.SEATS_RESERVED);
        return response;
    }

    public static Response createFindAllTripsResponse(List<Trip> trips) {
        logger.trace("Entering createFindAllTripsResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.FIND_ALL_TRIPS);
        response.setTrips(trips);
        return response;
    }

    public static Request createGetAllTripsRequest() {
        logger.trace("Entering createGetAllTripsRequest");
        Request request = new Request();
        request.setRequestType(RequestType.GET_ALL_TRIPS);
        return request;
    }

    public static Request createSearchTripSeatsRequest(String destination, LocalDate date, LocalTime time) {
        logger.trace("Entering createSearchTripSeatsRequest");
        Request request = new Request();
        request.setRequestType(RequestType.SEARCH_TRIP_SEATS);
        request.setTripDestination(destination);
        request.setTripDate(date);
        request.setTripTime(time);
        return request;
    }

    public static Request createReserveSeatsRequest(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) {
        logger.trace("Entering createReserveSeatsRequest");
        Request request = new Request();
        request.setRequestType(RequestType.RESERVE_SEATS);
        request.setClientName(clientName);
        request.setSeatsNumbers(seatNumbers);
        request.setTripToReserve(trip);
        request.setCurentEmployee(employee);
        return request;
    }

    public static Request createGetTripRequest(String destination, LocalDate date, LocalTime time) {
        logger.trace("Entering createGetTripRequest");
        Request request = new Request();
        request.setRequestType(RequestType.GET_TRIP);
        request.setDestination(destination);
        request.setTripDate(date);
        request.setTripTime(time);
        return request;
    }

    public static Response createSearchTripSeatsResponse(List<SeatDTO> seats) {
        logger.trace("Entering createSearchTripSeatsResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.FIND_TRIP_SEATS);
        response.setSeats(seats);
        return response;
    }

    public static Response createGetTripResponse(Trip trip) {
        logger.trace("Entering createGetTripResponse");
        Response response = new Response();
        response.setResponseType(ResponseType.FIND_TRIP);
        response.setTrip(trip);
        return response;
    }

    public static Request createLogoutRequest(Employee employee) {
        logger.trace("Entering createLogoutRequest");
        Request request = new Request();
        request.setRequestType(RequestType.LOGOUT);
        request.setCurentEmployee(employee);
        return request;
    }
}