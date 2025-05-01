package ro.mpp2024;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ClientRepository extends AbstractRepository<Integer, Client> implements IClientRepository {
//    public ClientRepository() {
//        super();
//    }

    public ClientRepository(Properties props) {
        super(props);
        logger.info("Initializing ClientRepository with properties: {} ",props);
    }

    @Override
    public Optional<Client> findById(Integer id) {
        logger.info("Find Client by ID: {}", id);
        String query = "SELECT * FROM Client WHERE id = ?";

        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Optional<Client> findByUsername(String username) {
        logger.info("Find Client by username: {}", username);
        String query = "SELECT * FROM Client WHERE name = ?";

        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                return Optional.of(new Client(id, username));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Client with username {}", username, e);
        }
        return Optional.empty();
    }

    public Optional<Client> findByName(String name) {
        logger.info("Find Client by name: {}", name);
        String query = "SELECT * FROM Client WHERE name = ?";

        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Iterable<Client> findAll() {
        logger.info("Find all Clients");
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM Client";

        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Optional<Client> save(Client client) {
        logger.info("Save Client: {}", client);
        String query = "INSERT INTO Client (name) VALUES (?)";

        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Optional<Client> delete(Integer id) {
        logger.info("Delete Client with ID: {}", id);
        String query = "DELETE FROM Client WHERE id = ?";

        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();
            return Optional.of(new Client(id, null));
        } catch (SQLException e) {
            logger.error("Database error while deleting Client with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Client> update(Client client) {
        logger.info("Update Client: {}", client);
        String query = "UPDATE Client SET name = ? WHERE id = ?";

        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, client.getName());
            statement.setInt(2, client.getId());
            statement.executeUpdate();
            return Optional.of(client);
        } catch (SQLException e) {
            logger.error("Database error while updating Client {}", client, e);
        }
        return Optional.empty();
    }
}