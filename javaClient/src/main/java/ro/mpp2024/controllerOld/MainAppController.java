package ro.mpp2024.controllerOld;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.mpp2024.model.Employee;
import ro.mpp2024.model.SeatDTO;
import ro.mpp2024.model.Trip;
import ro.mpp2024.service.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainAppController {

    @FXML
    private TableView<Trip> tripsTable;

    @FXML
    private TableColumn<Trip, String> destinationColumn;

    @FXML
    private TableColumn<Trip, LocalDate> dateColumn;

    @FXML
    private TableColumn<Trip, LocalTime> timeColumn;

    @FXML
    private TableColumn<Trip, Integer> seatsColumn;

    @FXML
    private TextField searchDestinationField;

    @FXML
    private DatePicker searchDateField;

    @FXML
    private TextField searchTimeField;

    @FXML
    private TextField clientNameField;

    @FXML
    private TextField seatNumbersField;

    @FXML
    private TableView<SeatDTO> seatsTable;

    @FXML
    private TableColumn<SeatDTO, Integer> seatNumberColumn;

    @FXML
    private TableColumn<SeatDTO, String> clientNameColumn;

    private Service service;
    private Trip tripToReserve;
    private Employee currentEmployee;

    public void setService(Service service) {
        this.service = service;
        loadTrips();

        // Configure columns for trips table
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("departureDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        seatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        // Configure columns for seats table
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
    }

    public void setCurrentEmployee(Employee employee) {
        this.currentEmployee = employee;
    }

    private void loadTrips() {
        List<Trip> trips = (List<Trip>) service.getAllTrips();
        tripsTable.getItems().clear();
        tripsTable.getItems().addAll(trips);
    }

    @FXML
    private void handleSearch() {
        String destination = searchDestinationField.getText();
        LocalDate date = searchDateField.getValue();
        LocalTime time = LocalTime.parse(searchTimeField.getText());


        // Get seats for the selected trip
        List<SeatDTO> allSeats = service.searchTripSeats(destination, date, time);
        tripToReserve = service.getTrip(destination, date, time);

        // Populate seats table
        seatsTable.getItems().clear();
        seatsTable.getItems().addAll(allSeats);

        for (SeatDTO s : allSeats) {
            System.out.println("Seat number: " + s.getSeatNumber() + ", Client name: " + s.getClientName());
        }

        loadTrips();

    }

    @FXML
    private void handleReserve() {
        String clientName = clientNameField.getText();
        String[] seatNumbers = seatNumbersField.getText().split(",");
        List<Integer> seats = List.of(seatNumbers).stream().map(Integer::parseInt).toList();

        try {
            System.out.println("curent emplo.getId");
            System.out.println(currentEmployee.getId());
            service.reserveSeats(clientName, seats, tripToReserve, currentEmployee); // Replace null with logged-in employee
            showAlert("Seats reserved successfully!");
            loadTrips();
        } catch (Exception e) {
            showAlert("Error reserving seats: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300); // Încarcă FXML-ul înainte de a obține controller-ul
            LoginController controller = loader.getController(); // Obține controller-ul după ce FXML-ul a fost încărcat
            controller.setService(service);

            Stage stage = (Stage) tripsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Error during logout: " + e.getMessage());
        }
    }
}