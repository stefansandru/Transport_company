package ro.mpp2024;

import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.mpp2024.controller.LoginController;
import ro.mpp2024.controller.MainAppController;
import ro.mpp2024.jsonProtocol.TaskManagementServicesJsonProxy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;



public class StartJsonFXClient extends Application {
    private Stage primaryStage;

    private static int defaultServerPort = 55555;
    private static String defaultServer = "localhost";

    private static final Logger logger = LogManager.getLogger(StartJsonFXClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();

        try {
            clientProps.load(StartJsonFXClient.class.getResourceAsStream("/client.properties"));
            logger.info("Client properties set {} ",clientProps);
        } catch (IOException e) {
            logger.error("Cannot find client.properties {}", String.valueOf(e));
            logger.debug("Looking for client.properties in folder {}",(new File(".")).getAbsolutePath());
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultServerPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            logger.error("Wrong port number {}", ex.getMessage());
            logger.debug("Using default port: {}", defaultServerPort);
        }
        logger.info("Using server IP {}", serverIP);
        logger.info("Using server port {}", serverPort);

        IServices server = new TaskManagementServicesJsonProxy(serverIP, serverPort);

        logger.info("Login view start loading");

        FXMLLoader loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/login-view.fxml"));
        loginLoader.setControllerFactory(param -> new LoginController(server));
        Parent root = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setServer(server);

        logger.info("Login view loaded");

        FXMLLoader mainAppLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/main-app-view.fxml"));
        mainAppLoader.setControllerFactory(param -> new MainAppController());
        Parent mainAppRoot = mainAppLoader.load();
        MainAppController MainAppController = mainAppLoader.getController();
        MainAppController.setServer(server);

        loginController.setMainAppController(MainAppController);
        loginController.setParent(mainAppRoot);

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
