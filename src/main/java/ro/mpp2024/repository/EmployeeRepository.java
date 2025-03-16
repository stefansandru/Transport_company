package ro.mpp2024.repository;

import ro.mpp2024.model.Employee;
import ro.mpp2024.model.Office;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeRepository implements Repository<Integer, Employee> {

    private final Logger logger = LoggerFactory.getLogger(EmployeeRepository.class);

    private final String url;
    private final String user;
    private final String password;
    private final OfficeRepository officeRepository;

    public EmployeeRepository(String url, String user, String password, OfficeRepository officeRepository) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.officeRepository = officeRepository;
    }

    @Override
    public Optional<Employee> findById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String query = "SELECT * FROM Employee WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer officeId = resultSet.getInt("office_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Office office = officeRepository.findById(officeId).orElse(null);
                return Optional.of(new Employee(id, username, password, office));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Employee with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT * FROM Employee";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Integer officeId = resultSet.getInt("office_id");
                Office office = officeRepository.findById(officeId).orElse(null);
                employees.add(new Employee(id, username, password, office));
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Employees", e);
        }
        return employees;
    }

    @Override
    public Optional<Employee> save(Employee employee) {
        String query = "INSERT INTO Employee (id, username, password, office_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getUsername());
            statement.setString(3, employee.getPassword());
            statement.setInt(4, employee.getOffice().getId());
            statement.executeUpdate();
            return Optional.of(employee);
        } catch (SQLException e) {
            logger.error("Database error while saving Employee with id {}", employee.getId(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> delete(Integer id) {
        String query = "DELETE FROM Employee WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.executeUpdate();
            return Optional.of(new Employee(id, null, null, null));
        } catch (SQLException e) {
            logger.error("Database error while deleting Employee with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> update(Employee employee) {
        String query = "UPDATE Employee SET username = ?, password = ?, office_id = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, employee.getUsername());
            statement.setString(2, employee.getPassword());
            statement.setInt(3, employee.getOffice().getId());
            statement.setInt(4, employee.getId());
            statement.executeUpdate();
            return Optional.of(employee);
        } catch (SQLException e) {
            logger.error("Database error while updating Employee with id {}", employee.getId(), e);
        }
        return Optional.empty();
    }
}