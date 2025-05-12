package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.services.TaskManagementSystemServicesImpl;
import ro.mpp2024.utils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartJsonServer {
    private static int defaultPort = 55555;
    private static final Logger logger = LogManager.getLogger(StartJsonServer.class);

    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try (InputStream input = StartJsonServer.class.getClassLoader().getResourceAsStream("server.properties")) {
            if (input == null) {
                logger.error("server.properties file not found in classpath");
                return;
            }
            serverProps.load(input);
            logger.info("Server properties set. {}", serverProps);
        } catch (IOException e) {
            logger.error("Error loading server.properties: {}", e.getMessage());
            logger.debug("Looking for file in {}", (new File(".")).getAbsolutePath());
            return;
        }
        // Initialize repositories
        DestinationRepository destinationRepository = new DestinationRepository(serverProps);
        OfficeRepository officeRepository = new OfficeRepository(serverProps);
        EmployeeRepository employeeRepository = new EmployeeRepository(serverProps, officeRepository);
        ClientRepository clientRepository = new ClientRepository(serverProps);
        TripRepository tripRepository = new TripRepository(serverProps, destinationRepository);
        ReservedSeatRepository reservedSeatRepository = new ReservedSeatRepository(
                serverProps, tripRepository, employeeRepository, clientRepository
        );

        // Initialize service
//        TaskManagementSystemServicesImpl serverImpl = new TaskManagementSystemServicesImpl(clientRepository, employeeRepository, reservedSeatRepository, tripRepository);

        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef){
            logger.error("Wrong  Port Number {}", nef.getMessage());
            logger.debug("Using default port {}", defaultPort);
        }
        logger.debug("Starting server on port: {}", serverPort);

//        AbstractServer server = new TaskManagementSystemJsonConcurrentServer(serverPort, serverImpl);
//        try {
//            server.start();
//        } catch (ServerException e) {
//            logger.error("Error starting the server {}", e.getMessage());
//        }
    }
}
