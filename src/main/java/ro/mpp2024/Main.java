package ro.mpp2024;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.controllerOld.LoginController;
import ro.mpp2024.model.Office;
import ro.mpp2024.repository.*;
import ro.mpp2024.service.Service;
import ro.mpp2024.utils.CryptPassword;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load properties
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("db.config")) {
            props.load(input);
        }

        // Initialize repositories
        DestinationRepository destinationRepository = new DestinationRepository(props);
        OfficeRepository officeRepository = new OfficeRepository(props);
        EmployeeRepository employeeRepository = new EmployeeRepository(props, officeRepository);
        ClientRepository clientRepository = new ClientRepository(props);
        TripRepository tripRepository = new TripRepository(props, destinationRepository);
        ReservedSeatRepository reservedSeatRepository = new ReservedSeatRepository(
                props, tripRepository, employeeRepository, clientRepository
        );

        // Initialize service
        Service service = new Service(clientRepository, employeeRepository, reservedSeatRepository, tripRepository);

        List<Office> offices = officeRepository.findAll();

        for (Office office : offices) {
            System.out.println(office.getName());
        }

        // Load the login view
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // Set the service in the LoginController
        LoginController controller = fxmlLoader.getController();
        controller.setService(service);

        // Set up the stage
        stage.setTitle("Transport Reservation System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Example of hashing a password
        String plainPassword = "padi";
        String hashedPassword = CryptPassword.hashPassword(plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);

        // Launch the JavaFX application
        launch();
    }
}