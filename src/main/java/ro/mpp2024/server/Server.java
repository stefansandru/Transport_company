package ro.mpp2024.server;

import ro.mpp2024.repository.*;
import ro.mpp2024.service.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port = 12345;
    private final ExecutorService executorService;
    private final Service service;

    public Server(Service service) {
        this.service = service;
        this.executorService = Executors.newFixedThreadPool(10); // Max 10 clienți
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                executorService.submit(new ServerHandler(clientSocket, service, this));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    public synchronized void notifyClients(String destination, String date, String time) {
        // Metodă pentru a notifica clienții (va fi implementată în ServerHandler)
        ServerHandler.notifyAllClients(destination, date, time);
    }

    public static void main(String[] args) {
        // Încarcă proprietățile și inițializează repository-urile
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("db.config")) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            return;
        }

        DestinationRepository destinationRepository = new DestinationRepository(props);
        OfficeRepository officeRepository = new OfficeRepository(props);
        EmployeeRepository employeeRepository = new EmployeeRepository(props, officeRepository);
        ClientRepository clientRepository = new ClientRepository(props);
        TripRepository tripRepository = new TripRepository(props, destinationRepository);
        ReservedSeatRepository reservedSeatRepository = new ReservedSeatRepository(
                props, tripRepository, employeeRepository, clientRepository
        );

        Service service = new Service(clientRepository, employeeRepository, reservedSeatRepository, tripRepository);
        Server server = new Server(service);
        server.start();
    }
}