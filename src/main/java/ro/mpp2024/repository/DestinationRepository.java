package ro.mpp2024.repository;

import ro.mpp2024.model.Destination;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestinationRepository implements Repository<Integer, Destination> {

    Logger logger = LoggerFactory.getLogger(DestinationRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public DestinationRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
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

    @Override
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

    @Override
    public Optional<Destination> save(Destination destination) {
        String query = "INSERT INTO Destination (name) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, destination.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database error while saving Destination {}", destination, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Destination> delete(Integer id) {
        String query = "DELETE FROM Destination WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();
            return Optional.of(new Destination(id, null));
        } catch (SQLException e) {
            logger.error("Database error while deleting Destination with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Destination> update(Destination destination) {
        String query = "UPDATE Destination SET name = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, destination.getName());
            statement.setInt(2, destination.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database error while updating Destination {}", destination, e);
        }
        return Optional.empty();
    }

}
