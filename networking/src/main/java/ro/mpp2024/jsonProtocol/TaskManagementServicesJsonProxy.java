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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// todo: remove abstract
public abstract class TaskManagementServicesJsonProxy extends AbstractServicesImpl {
    private String host;
    private int port;
    private Socket connection;


    private IObserver client;

    private BufferedReader in;
    private PrintWriter out;
    private Gson formatter;

    private BlockingQueue<Response> responseQueue;
    private volatile boolean finished;

    private static final Logger logger = LogManager.getLogger(TaskManagementServicesJsonProxy.class);

    public TaskManagementServicesJsonProxy(String host, int port) {
        logger.trace("Entering TaskManagementServicesJsonProxy constructor");
        logger.info("Proxy constructor");
        this.host = host;
        this.port = port;
        responseQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public Employee login(String username, String password, IObserver client) throws ServicesException {
        logger.trace("Entering login");
        initializeConnection();
        Request request = JsonProtocolUtils.createLoginRequest(username, password);
        sendRequest(request);
        Response response = readResponse();
        System.out.println("\n");
        if (response.getResponseType() == ResponseType.EMPLOYEE_LOGGED_IN) {
            logger.info("Login successful, response: {}", response.getResponseType());
            logger.info("client: {}", client);
            this.client = client;
        }
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            closeConnection();
            throw new ServicesException(errorMessage);
        }
        return response.getLoggedEmployee();
    }

    @Override
    public void logout(Employee employee) throws ServicesException {
        logger.trace("Entering logout");
        Request request = JsonProtocolUtils.createLogoutRequest(employee);
        sendRequest(request);
        Response response = readResponse();
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            closeConnection();
            throw new ServicesException(errorMessage);
        }
        closeConnection();
    }

    @Override
    public List<Trip> getAllTrips() throws ServicesException {
        logger.trace("Entering getAllTrips");
        initializeConnection();
        Request request = JsonProtocolUtils.createGetAllTripsRequest();
        sendRequest(request);
        Response response = readResponse();
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            throw new ServicesException(errorMessage);
        }
        return response.getTrips();
    }

    @Override
    public List<SeatDTO> searchTripSeats(String destination, LocalDate date, LocalTime time) throws ServicesException {
        logger.trace("Entering searchTripSeats");
        initializeConnection();
        Request request = JsonProtocolUtils.createSearchTripSeatsRequest(destination, date, time);
        sendRequest(request);
        Response response = readResponse();
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            throw new ServicesException(errorMessage);
        }
        return response.getSeats();
    }

    @Override
    public void reserveSeats(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) throws ServicesException {
        logger.trace("Entering reserveSeats");
        initializeConnection();
        Request request = JsonProtocolUtils.createReserveSeatsRequest(clientName, seatNumbers, trip, employee);
        sendRequest(request);
        Response response = readResponse();
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            throw new ServicesException(errorMessage);
        }
    }

    @Override
    public Trip getTrip(String destination, LocalDate date, LocalTime time) throws ServicesException {
        logger.trace("Entering getTrip");
        initializeConnection();
        Request request = JsonProtocolUtils.createGetTripRequest(destination, date, time);
        sendRequest(request);
        Response response = readResponse();
        if(response.getResponseType() == ResponseType.ERROR) {
            String errorMessage = response.getErrorMessage();
            throw new ServicesException(errorMessage);
        }
        return response.getTrip();
    }

    private void initializeConnection() throws ServicesException {
        logger.trace("Entering initializeConnection");
        if (connection != null && connection.isConnected() && !connection.isClosed()) {
            logger.info("Connection already initialized");
            return;
        }
        try {
            logger.info("init connection");
            formatter = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                    .create();
            connection = new Socket(host,port);
            out = new PrintWriter(connection.getOutputStream());
            out.flush();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;
            startReader();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private void closeConnection() {
        logger.trace("Entering closeConnection");
        finished = true;
        try {
            in.close();
            out.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }

    private void sendRequest(Request request) throws ServicesException {
        logger.trace("Entering sendRequest");
        String reqLine = formatter.toJson(request);
        logger.info(reqLine);
        try {
            out.println(reqLine);
            out.flush();
        } catch (Exception e) {
            throw new ServicesException("Error sending object "+e);
        }

    }

    private Response readResponse() throws ServicesException {
        logger.trace("Entering readResponse");
        Response response = null;
        logger.info("set to null");
        try {
            logger.info("getting response...");
            response = responseQueue.take();
            logger.info("Response taken: [{}]", response);
        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }

    private boolean isUpdate(Response response) {
        logger.trace("Entering isUpdate");
        return response.getResponseType() == ResponseType.SEATS_RESERVED;
//        return false;
    }

    private void handleUpdate(Response response) {
        logger.trace("Entering handleUpdate");
//        if (response.getResponseType() == ResponseType.EMPLOYEE_LOGGED_IN){
//            Employee employee = DTOUtils.getEmployee(response.getLoggedInEmployeeDTO());
//            logger.debug("Employee logged in {}", employee);
//            try {
//                client.employeeLoggedIn(employee);
//            } catch (ServicesException e) {
//                logger.error(e);
//                logger.error(e.getStackTrace());
//            }
//        }

        if (response.getResponseType() == ResponseType.SEATS_RESERVED){
//            List<SeatDTO> seats = response.getSeats();
//            logger.debug("Seats reserved {}", seats);
            try {
                logger.debug("Seats reserved {}", response.getSeats());
                logger.debug("client {}", client);
                client.seatsReserved();
            } catch (ServicesException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            logger.trace("Entering ReaderThread.run");
            while(!finished){
                try {
                    String responseLine = in.readLine();
                    logger.debug("Response received: [{}]", responseLine);
                    if (responseLine == null) {
                        logger.error("Received null from server! Connection may be closed.");
                        break;
                    }
                    Response response = formatter.fromJson(responseLine, Response.class);
                    if (response == null) {
                        logger.error("Failed to parse response: [{}]", responseLine);
                        continue;
                    }
                    if (isUpdate(response)){
                        handleUpdate(response);
                    } else {
                        try {
                            responseQueue.put(response);
                        } catch (InterruptedException e) {
                            logger.error(e);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Reading error {}", e.toString());
                    break;
                }
            }
        }
    }

    private void startReader() {
        logger.trace("Entering startReader");
        Thread tw = new Thread(new ReaderThread());
        tw.start();
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
//            logger.trace("Entering LocalDateTimeAdapter.read");
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