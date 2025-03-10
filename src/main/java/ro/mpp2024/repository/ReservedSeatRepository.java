package ro.mpp2024.repository;

import ro.mpp2024.model.ReservedSeat;
import ro.mpp2024.model.Trip;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReservedSeatRepository {

    private final Logger logger = LoggerFactory.getLogger(ReservedSeatRepository.class);

    private final String url;
    private final String user;
    private final String password;
    private final TripRepository tripRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public ReservedSeatRepository(String url, String user, String password,
                                  TripRepository tripRepository,
                                  EmployeeRepository employeeRepository,
                                  ClientRepository clientRepository) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.tripRepository = tripRepository;
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    public Optional<ReservedSeat> findById(Integer id) {
        String query = "SELECT * FROM ReservedSeats WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return extractReservedSeatFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding ReservedSeat with id {}", id, e);
        }
        return Optional.empty();
    }

    public List<ReservedSeat> findAll() {
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        String query = "SELECT * FROM ReservedSeats";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                extractReservedSeatFromResultSet(resultSet).ifPresent(reservedSeats::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all ReservedSeats", e);
        }
        return reservedSeats;
    }

    public List<ReservedSeat> findByTripId(Integer tripId) {
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        String query = "SELECT * FROM ReservedSeats WHERE trip_id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, tripId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                extractReservedSeatFromResultSet(resultSet).ifPresent(reservedSeats::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding ReservedSeats for trip id {}", tripId, e);
        }
        return reservedSeats;
    }

    public boolean save(ReservedSeat reservedSeat) {
        String query = "INSERT INTO ReservedSeats(trip_id, employee_id, seat_number, client_id) VALUES(?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, reservedSeat.getTrip().getId());

            if (reservedSeat.getEmployee() != null) {
                statement.setInt(2, reservedSeat.getEmployee().getId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setInt(3, reservedSeat.getSeatNumber());

            if (reservedSeat.getClient() != null) {
                statement.setInt(4, reservedSeat.getClient().getId());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservedSeat.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while saving ReservedSeat", e);
        }
        return false;
    }

    public boolean delete(Integer id) {
        String query = "DELETE FROM ReservedSeats WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Database error while deleting ReservedSeat with id {}", id, e);
            return false;
        }
    }

    private Optional<ReservedSeat> extractReservedSeatFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        Integer tripId = resultSet.getInt("trip_id");
        Integer employeeId = resultSet.getInt("employee_id");
        Integer seatNumber = resultSet.getInt("seat_number");
        Integer clientId = resultSet.getInt("client_id");
        Timestamp reservationTimestamp = resultSet.getTimestamp("reservation_date");
        LocalDateTime reservationDate = reservationTimestamp != null ?
                                       reservationTimestamp.toLocalDateTime() : null;

        Optional<Trip> tripOpt = tripRepository.findById(tripId);

        if (tripOpt.isPresent()) {
            ReservedSeat reservedSeat = new ReservedSeat();
            reservedSeat.setId(id);
            reservedSeat.setTrip(tripOpt.get());
            reservedSeat.setSeatNumber(seatNumber);
            reservedSeat.setReservationDate(reservationDate);

            if (employeeId != 0 && !resultSet.wasNull()) {
                employeeRepository.findById(employeeId).ifPresent(reservedSeat::setEmployee);
            }

            if (clientId != 0 && !resultSet.wasNull()) {
                clientRepository.findById(clientId).ifPresent(reservedSeat::setClient);
            }

            return Optional.of(reservedSeat);
        }

        return Optional.empty();
    }
}