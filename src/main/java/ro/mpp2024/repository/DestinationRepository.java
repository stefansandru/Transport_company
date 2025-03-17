package ro.mpp2024.repository;

import ro.mpp2024.model.Destination;

import java.sql.*;
import java.util.*;

public class DestinationRepository extends AbstractRepository<Integer, Destination> implements IRepository<Integer, Destination> {

    public DestinationRepository(Properties props) {
        super(props);
    }

    @Override
    public Optional<Destination> findById(Integer id) {
        logger.info("Find Destination by ID: {}", id);
        String query = "SELECT * FROM Destination WHERE id = ?";

        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find Destination by name: {}", name);
        String query = "SELECT * FROM Destination WHERE name = ?";

        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find all Destinations");
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM Destination";

        try (Connection connection = jdbc.getConnection();
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
        logger.info("Save Destination: {}", destination);
        String query = "INSERT INTO Destination (name) VALUES (?)";

        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, destination.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                Integer id = resultSet.getInt(1);
                destination.setId(id);
                return Optional.of(destination);
            }
        } catch (SQLException e) {
            logger.error("Database error while saving Destination {}", destination, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Destination> delete(Integer id) {
        logger.info("Delete Destination with ID: {}", id);
        Optional<Destination> destination = findById(id);
        String query = "DELETE FROM Destination WHERE id = ?";

        if (destination.isPresent()) {
            try (Connection connection = jdbc.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, id);
                statement.executeUpdate();
                return destination;
            } catch (SQLException e) {
                logger.error("Database error while deleting Destination with id {}", id, e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Destination> update(Destination destination) {
        logger.info("Update Destination: {}", destination);
        String query = "UPDATE Destination SET name = ? WHERE id = ?";

        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, destination.getName());
            statement.setInt(2, destination.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return Optional.of(destination);
            }
        } catch (SQLException e) {
            logger.error("Database error while updating Destination {}", destination, e);
        }
        return Optional.empty();
    }
}