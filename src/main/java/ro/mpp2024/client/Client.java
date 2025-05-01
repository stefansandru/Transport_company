package ro.mpp2024.client;

import ro.mpp2024.model.Employee;
import ro.mpp2024.model.SeatDTO;
import ro.mpp2024.model.Trip;
import ro.mpp2024.network.Message;
import ro.mpp2024.network.Protocol;

import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

public class Client {
    private final String host = "localhost";
    private final int port = 12345;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<List<SeatDTO>> updateCallback;

    public Client() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            startListener();
        } catch (IOException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage());
        }
    }

    public void setUpdateCallback(Consumer<List<SeatDTO>> callback) {
        this.updateCallback = callback;
    }

    private void startListener() {
        new Thread(() -> {
            try {
                while (true) {
                    Message message = (Message) in.readObject();
                    if (message.getType().equals(Protocol.UPDATE_SEATS)) {
                        List<SeatDTO> seats = (List<SeatDTO>) message.getPayload();
                        if (updateCallback != null) {
                            updateCallback.accept(seats);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Client listener error: " + e.getMessage());
            }
        }).start();
    }

    private Message sendRequest(Message request) throws IOException, ClassNotFoundException {
        synchronized (out) {
            out.writeObject(request);
            out.flush();
        }
        return (Message) in.readObject();
    }

    public Employee login(String username, String password) throws Exception {
        Message response = sendRequest(new Message(Protocol.LOGIN, new String[]{username, password}));
        if (response.getType().equals(Protocol.ERROR)) {
            throw new Exception((String) response.getPayload());
        }
        return (Employee) response.getPayload();
    }

    public List<Trip> getAllTrips() throws Exception {
        Message response = sendRequest(new Message(Protocol.GET_TRIPS, null));
        if (response.getType().equals(Protocol.ERROR)) {
            throw new Exception((String) response.getPayload());
        }
        return (List<Trip>) response.getPayload();
    }

    public List<SeatDTO> searchTripSeats(String destination, LocalDate date, LocalTime time) throws Exception {
        Message response = sendRequest(new Message(Protocol.SEARCH_TRIP, new Object[]{destination, date, time}));
        if (response.getType().equals(Protocol.ERROR)) {
            throw new Exception((String) response.getPayload());
        }
        return (List<SeatDTO>) response.getPayload();
    }

    public void reserveSeats(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) throws Exception {
        Message response = sendRequest(new Message(Protocol.RESERVE, new Object[]{clientName, seatNumbers, trip, employee}));
        if (response.getType().equals(Protocol.ERROR)) {
            throw new Exception((String) response.getPayload());
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}