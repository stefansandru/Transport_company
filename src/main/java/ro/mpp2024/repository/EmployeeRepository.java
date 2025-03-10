package ro.mpp2024.repository;

import ro.mpp2024.model.Employee;
import ro.mpp2024.model.Office;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeRepository {

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

    public Optional<Employee> findById(Integer id) {
        String query = "SELECT * FROM Employee WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                Integer officeId = resultSet.getInt("office_id");

                Optional<Office> officeOpt = officeRepository.findById(officeId);
                if (officeOpt.isPresent()) {
                    return Optional.of(new Employee(id, username, password, officeOpt.get()));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Employee with id {}", id, e);
        }
        return Optional.empty();
    }

    public Optional<Employee> findByUsername(String username) {
        String query = "SELECT * FROM Employee WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String password = resultSet.getString("password");
                Integer officeId = resultSet.getInt("office_id");

                Optional<Office> officeOpt = officeRepository.findById(officeId);
                if (officeOpt.isPresent()) {
                    return Optional.of(new Employee(id, username, password, officeOpt.get()));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Employee with username {}", username, e);
        }
        return Optional.empty();
    }

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

                Optional<Office> officeOpt = officeRepository.findById(officeId);
                if (officeOpt.isPresent()) {
                    employees.add(new Employee(id, username, password, officeOpt.get()));
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Employees", e);
        }
        return employees;
    }
}