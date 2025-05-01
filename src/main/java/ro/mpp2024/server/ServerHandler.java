package ro.mpp2024.server;

import ro.mpp2024.Employee;
import ro.mpp2024.Trip;
import ro.mpp2024.network.Message;
import ro.mpp2024.network.Protocol;
import ro.mpp2024.service.Service;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerHandler implements Runnable {
    private static final List<ServerHandler> clients = new CopyOnWriteArrayList<>();
    private final Socket socket;
    private final Service service;
    private final Server server;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ServerHandler(Socket socket, Service service, Server server) {
        this.socket = socket;
        this.service = service;
        this.server = server;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error initializing streams: " + e.getMessage());
        }
        clients.add(this);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message request = (Message) in.readObject();
                handleRequest(request);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            clients.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleRequest(Message request) throws IOException {
        String type = request.getType();
        Object payload = request.getPayload();

        try {
            switch (type) {
                case Protocol.LOGIN:
                    String[] loginData = (String[]) payload;
                    Employee employee = service.login(loginData[0], loginData[1]);
                    sendResponse(new Message(Protocol.SUCCESS, employee));
                    break;

                case Protocol.GET_TRIPS:
                    Iterable<Trip> trips = service.getAllTrips();
                    sendResponse(new Message(Protocol.SUCCESS, new ArrayList<>((List<Trip>) trips)));
                    break;

                case Protocol.SEARCH_TRIP:
                    Object[] searchData = (Object[]) payload;
                    String destination = (String) searchData[0];
                    LocalDate date = (LocalDate) searchData[1];
                    LocalTime time = (LocalTime) searchData[2];
                    List<SeatDTO> seats = service.searchTripSeats(destination, date, time);
                    sendResponse(new Message(Protocol.SUCCESS, seats));
                    break;

                case Protocol.RESERVE:
                    Object[] reserveData = (Object[]) payload;
                    String clientName = (String) reserveData[0];
                    List<Integer> seatNumbers = (List<Integer>) reserveData[1];
                    Trip trip = (Trip) reserveData[2];
                    Employee emp = (Employee) reserveData[3];
                    service.reserveSeats(clientName, seatNumbers, trip, emp);
                    sendResponse(new Message(Protocol.SUCCESS, null));
                    // Notifică ceilalți clienți
                    server.notifyClients(trip.getDestination(), trip.getDepartureDate().toString(), trip.getDepartureTime().toString());
                    break;

                default:
                    sendResponse(new Message(Protocol.ERROR, "Unknown request type"));
            }
        } catch (Exception e) {
            sendResponse(new Message(Protocol.ERROR, e.getMessage()));
        }
    }

    private void sendResponse(Message response) throws IOException {
        synchronized (out) {
            out.writeObject(response);
            out.flush();
        }
    }

    public static void notifyAllClients(String destination, String date, String time) {
        for (ServerHandler client : clients) {
            try {
                List<SeatDTO> seats = client.service.searchTripSeats(
                        destination, LocalDate.parse(date), LocalTime.parse(time));
                client.sendResponse(new Message(Protocol.UPDATE_SEATS, seats));
            } catch (IOException e) {
                System.err.println("Error notifying client: " + e.getMessage());
            }
        }
    }
}