package ro.mpp2024.repository;

import ro.mpp2024.model.Client;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRepository {

    Logger logger = LoggerFactory.getLogger(ClientRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public ClientRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Optional<Client> findById(Integer id) {
        String query = "SELECT * FROM Client WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return Optional.of(new Client(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Client with id {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Client> findByName(String name) {
        String query = "SELECT * FROM Client WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Client(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Client with name {}", name, e);
        }
        return Optional.empty();
    }

    public Iterable<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM Client";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                clients.add(new Client(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Clients", e);
        }
        return clients;
    }

    public Optional<Client> save(Client client) {
        String query = "INSERT INTO Client (name) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, client.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                Integer id = resultSet.getInt(1);
                return Optional.of(new Client(id, client.getName()));
            }
        } catch (SQLException e) {
            logger.error("Database error while saving Client {}", client, e);
        }
        return Optional.empty();
    }

}
