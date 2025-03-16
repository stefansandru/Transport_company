package ro.mpp2024.repository;

import ro.mpp2024.model.Office;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfficeRepository implements Repository<Integer, Office> {

    private final Logger logger = LoggerFactory.getLogger(OfficeRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public OfficeRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Optional<Office> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

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

    @Override
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

    @Override
    public Optional<Office> save(Office office) {
        if (office == null) {
            throw new IllegalArgumentException("Office must not be null");
        }

        String query = "INSERT INTO Office (id, name) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, office.getId());
            statement.setString(2, office.getName());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(office);
            }
        } catch (SQLException e) {
            logger.error("Database error while saving Office: {}", office, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Office> delete(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        Optional<Office> officeToDelete = findById(id);
        if (officeToDelete.isEmpty()) {
            return Optional.empty();
        }

        String query = "DELETE FROM Office WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                return officeToDelete;
            }
        } catch (SQLException e) {
            logger.error("Database error while deleting Office with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Office> update(Office office) {
        if (office == null) {
            throw new IllegalArgumentException("Office must not be null");
        }

        String query = "UPDATE Office SET name = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, office.getName());
            statement.setInt(3, office.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(office);
            }
        } catch (SQLException e) {
            logger.error("Database error while updating Office: {}", office, e);
        }
        return Optional.empty();
    }
}