package ro.mpp2024;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class OfficeRepository extends AbstractRepository<Integer, Office> implements IOfficeRepository {

    public OfficeRepository(Properties props) {
        super(props);
    }

    @Override
    public Optional<Office> findById(Integer id) {
        logger.info("Find Office by ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String query = "SELECT * FROM Office WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find all Offices");
        List<Office> offices = new ArrayList<>();
        String query = "SELECT * FROM Office";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Save Office: {}", office);
        if (office == null) {
            throw new IllegalArgumentException("Office must not be null");
        }

        String query = "INSERT INTO Office (id, name) VALUES (?, ?)";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Delete Office with ID: {}", id);
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        Optional<Office> officeToDelete = findById(id);
        if (officeToDelete.isEmpty()) {
            return Optional.empty();
        }

        String query = "DELETE FROM Office WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Update Office: {}", office);
        if (office == null) {
            throw new IllegalArgumentException("Office must not be null");
        }

        String query = "UPDATE Office SET name = ? WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, office.getName());
            statement.setInt(2, office.getId()); // Fixed index from 3 to 2

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(office);
            }
        } catch (SQLException e) {
            logger.error("Database error while updating Office: {}", office, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Office> findByName(String name) {
        logger.info("Find Office by name: {}", name);
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }

        String query = "SELECT * FROM Office WHERE name = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                return Optional.of(new Office(id, name));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Office with name {}", name, e);
        }
        return Optional.empty();
    }
}