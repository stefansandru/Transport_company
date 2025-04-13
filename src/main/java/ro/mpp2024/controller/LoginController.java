package ro.mpp2024.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ro.mpp2024.service.Service;
import ro.mpp2024.model.Employee;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label errorLabel;

    private Service service;


    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Employee loggedInEmployee = service.login(username, password);
            openMainApp(loggedInEmployee);
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("An error occurred during login: " + e.getMessage());
            System.err.println("Error during login: " + e.getMessage());
        }
    }

    private void openMainApp(Employee loggedInEmployee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-app-view.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 800, 600);
            MainAppController controller = loader.getController();
            controller.setService(service);
            controller.setCurrentEmployee(loggedInEmployee); // Set the current employee
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading main application: " + e.getMessage());
        }
    }
}