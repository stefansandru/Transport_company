package ro.mpp2024.repository;

import ro.mpp2024.model.Destination;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestinationRepository {

    Logger logger = LoggerFactory.getLogger(DestinationRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public DestinationRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Optional<Destination> findById(Integer id) {
        String query = "SELECT * FROM Destination WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return Optional.of(new Destination(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Destination with id {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Destination> findByName(String name) {
        String query = "SELECT * FROM Destination WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Destination(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Destination with name {}", name, e);
        }
        return Optional.empty();
    }

    public List<Destination> findAll() {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM Destination";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                destinations.add(new Destination(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Destinations", e);
        }
        return destinations;
    }

}
