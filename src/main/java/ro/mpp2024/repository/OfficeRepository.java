package ro.mpp2024.repository;

import ro.mpp2024.model.Office;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfficeRepository {

    private static final Logger logger = LoggerFactory.getLogger(OfficeRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public OfficeRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Optional<Office> findById(Integer id) {
        String query = "SELECT * FROM Office WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                return Optional.of(new Office(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Office with id {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Office> findByName(String name) {
        String query = "SELECT * FROM Office WHERE name = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Office(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Office with name {}", name, e);
        }
        return Optional.empty();
    }

    public List<Office> findAll() {
        List<Office> offices = new ArrayList<>();
        String query = "SELECT * FROM Office";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                offices.add(new Office(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Offices", e);
        }
        return offices;
    }
}