package ro.mpp2024.jsonProtocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.*;
import ro.mpp2024.dto.DTOUtils;
import ro.mpp2024.dto.EmployeeDTO;
import ro.mpp2024.dto.TripDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class TaskManagementSystemJsonWorker implements Runnable, IObserver {
    private final IServices server;
    private final Socket connection;

    private BufferedReader in;
    private PrintWriter out;
    private final Gson formatter;
    private volatile boolean connected;

    private static final Logger logger = LogManager.getLogger(TaskManagementSystemJsonWorker.class);

    public TaskManagementSystemJsonWorker(IServices server, Socket connection) {
        logger.trace("Entering TaskManagementSystemJsonWorker constructor");
        this.server = server;
        this.connection = connection;
        this.formatter = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            out = new PrintWriter(connection.getOutputStream());
            connected = true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void run() {
        logger.trace("Entering run");
        while(connected){
            try {
                logger.debug("Waiting for client request...");
                String requestLine = in.readLine();
                logger.debug("Received request line: {}", requestLine);
                if (requestLine == null) {
                    logger.warn("Client closed connection");
                    connected = false;
                    continue;
                }
                Request request = formatter.fromJson(requestLine, Request.class);
                logger.debug("Parsed request type: {}", request.getRequestType());
                Response response = handleRequest(request);
                logger.debug("Request handled, response: {}", response);
                if (response != null){
                    sendResponse(response);
                } else {
                    logger.warn("No response generated for request: {}", request.getRequestType());
                }
                
            } catch (IOException e) {
                logger.error("Error processing client request: {}", e.getMessage());
                logger.error("Stack trace: ", e);
                connected = false;
            }
            
            // Additional logging for connection state
            logger.debug("Connection state at end of loop: {}", connected ? "Connected" : "Disconnected");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Thread sleep interrupted: {}", e.getMessage());
            }
        }
        
        // Log when worker thread exits
        logger.info("Worker thread exiting, cleaning up resources");
        
        try {
            in.close();
            out.close();
            connection.close();
            logger.debug("Connection resources closed");
        } catch (IOException e) {
            logger.error("Error closing connection: {}", e.getMessage());
        }
    }

    public void seatsReserved() throws ServicesException {
        logger.trace("Entering seatsReserved");
        Response resp = JsonProtocolUtils.createSeatsReservedResponse();
        logger.debug("Seats reserved aodfihvoioqihv {}", resp);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error(e);
            throw new ServicesException("Error: " + e);
        }
    }

    @Override
    public void employeeLoggedIn(Employee employee) throws ServicesException {
        logger.trace("Entering employeeLoggedIn");
        Response resp = JsonProtocolUtils.createEmployeeLoggedInResponse(employee);
        logger.debug("Employee logged in {}", resp);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error(e);
            throw new ServicesException("Error: " + e);
        }
    }

    private final Response okResponse = JsonProtocolUtils.createOkResponse();

    private Response handleRequest(Request request) {
        logger.trace("Entering handleRequest");

        if (request.getRequestType() == RequestType.LOGIN) {
            logger.debug("Processing login request for username: {}", request.getUsername());
            String username = request.getUsername();
            String password = request.getPassword();
            try {
                logger.debug("Calling server.login with username: {}", username);
                Employee loggedInEmployee = server.login(username, password, this);
                logger.debug("Login successful for employee: {}", loggedInEmployee.getUsername());
                logger.debug("Creating login response with employee ID: {}", loggedInEmployee.getId());
                Response response = JsonProtocolUtils.createEmployeeLoggedInResponse(loggedInEmployee);
                logger.debug("Created response: {}", response);
                return response;
            } catch (ServicesException e) {
                logger.error("Login failed: {}", e.getMessage());
                connected = false;
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }

        } else if (request.getRequestType() == RequestType.GET_ALL_TRIPS) {
            logger.debug("Processing get trips request");
            try {
                List<Trip> trips = server.getAllTrips();
                logger.debug("Retrieved trips: {}", trips);
                Response response = JsonProtocolUtils.createFindAllTripsResponse(trips);
                logger.debug("Created response: {}", response);
                return response;
            } catch (ServicesException e) {
                logger.error("Error retrieving trips: {}", e.getMessage());
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }

        } else if (request.getRequestType() == RequestType.GET_TRIP) {
            logger.debug("Processing get trip request");
            String destination = request.getDestination();
            LocalDate tripDate = request.getTripDate();
            LocalTime tripTime = request.getTripTime();
            try {
                Trip trip = server.getTrip(destination, tripDate, tripTime);
                logger.debug("Retrieved trip: {}", trip);
                Response response = JsonProtocolUtils.createGetTripResponse(trip);
                logger.debug("Created response: {}", response);
                return response;
            } catch (ServicesException e) {
                logger.error("Error retrieving trip: {}", e.getMessage());
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }

        } else if (request.getRequestType() == RequestType.SEARCH_TRIP_SEATS) {
            logger.debug("Processing search trip seats request");
            String destination = request.getTripDestination();
            LocalDate tripDate = request.getTripDate();
            LocalTime tripTime = request.getTripTime();
            try {
                logger.debug("Calling server.searchTripSeats with destination: {}, date: {}, time: {}", destination, tripDate, tripTime);
                List<SeatDTO> seats = server.searchTripSeats(destination, tripDate, tripTime);
                logger.debug("Retrieved seats: {}", seats);
                Response response = JsonProtocolUtils.createSearchTripSeatsResponse(seats);
                logger.debug("Created response: {}", response);
                return response;
            } catch (ServicesException e) {
                logger.error("Error searching trip seats: {}", e.getMessage());
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }

        } else if (request.getRequestType() == RequestType.RESERVE_SEATS) {
            logger.debug("Processing reserve seats request");
            String clientName = request.getClientName();
            List<Integer> seatNumbers = request.getSeatsNumbers();
            Trip trip = request.getTripToReserve();
            Employee employee = request.getCurentEmployee();
            try {
                server.reserveSeats(clientName, seatNumbers, trip, employee);
                logger.debug("Seats reserved successfully ieubciuebcieru");
//                Response response = JsonProtocolUtils.createSeatsReservedResponse();
//                logger.debug("Created response iweieufirt: {}", response);
//                return response;
                return okResponse;
            } catch (ServicesException e) {
                logger.error("Error reserving seats: {}", e.getMessage());
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }

        } else if (request.getRequestType() == RequestType.LOGOUT) {
            logger.debug("Processing logout request");
            try {
                server.logout(request.getCurentEmployee());
                connected = false;
                logger.debug("Logout successful");
                return okResponse;
            } catch (ServicesException e) {
                logger.error("Error during logout: {}", e.getMessage());
                Response errorResponse = JsonProtocolUtils.createErrorResponse(e.getMessage());
                logger.debug("Created error response: {}", errorResponse);
                return errorResponse;
            }
        }

        logger.warn("Unhandled request type: {}", request.getRequestType());
        return null;
    }

    private void sendResponse(Response response) throws IOException{
        logger.trace("Entering sendResponse");
        logger.debug("Response to be serialized: {}", response);
        String responseLine = formatter.toJson(response);
        logger.debug("Serialized response: {}", responseLine);
        
        synchronized (out) {
            logger.debug("Sending response to client...");
            out.println(responseLine);
            out.flush();
            logger.debug("Response sent to client");
        }
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            logger.trace("Entering LocalDateTimeAdapter.write");
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString()); // Serializes to ISO-8601, e.g., "2025-04-06T12:34:56"
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            logger.trace("Entering LocalDateTimeAdapter.read");
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString()); // Deserializes from ISO-8601
        }
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
//            logger.trace("Entering LocalDateAdapter.write");
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString()); // Serializes to ISO-8601, e.g., "2025-04-06"
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
//            logger.trace("Entering LocalDateAdapter.read");
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString()); // Deserializes from ISO-8601
        }
    }

    private static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
//            logger.trace("Entering LocalTimeAdapter.write");
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString()); // Serializes to ISO-8601, e.g., "12:34:56"
            }
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
//            logger.trace("Entering LocalTimeAdapter.read");
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalTime.parse(in.nextString()); // Deserializes from ISO-8601
        }
    }
}