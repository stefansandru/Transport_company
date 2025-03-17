package ro.mpp2024.repository;

import ro.mpp2024.model.ReservedSeat;
import ro.mpp2024.model.Trip;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservedSeatRepository extends AbstractRepository<Integer, ReservedSeat> implements IRepository<Integer, ReservedSeat> {

    private final TripRepository tripRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public ReservedSeatRepository(Properties props,
                                  TripRepository tripRepository,
                                  EmployeeRepository employeeRepository,
                                  ClientRepository clientRepository) {
        super(props);
        this.tripRepository = tripRepository;
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<ReservedSeat> findById(Integer id) {
        logger.info("Find ReservedSeat by ID: {}", id);
        String query = "SELECT * FROM ReservedSeats WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
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

    @Override
    public List<ReservedSeat> findAll() {
        logger.info("Find all ReservedSeats");
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        String query = "SELECT * FROM ReservedSeats";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find ReservedSeats by Trip ID: {}", tripId);
        List<ReservedSeat> reservedSeats = new ArrayList<>();
        String query = "SELECT * FROM ReservedSeats WHERE trip_id = ?";
        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Optional<ReservedSeat> save(ReservedSeat reservedSeat) {
        logger.info("Save ReservedSeat: {}", reservedSeat);
        String query = "INSERT INTO ReservedSeats(trip_id, employee_id, seat_number, client_id, reservation_date) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, reservedSeat.getTrip().getId());
            statement.setInt(2, reservedSeat.getEmployee().getId());
            statement.setInt(3, reservedSeat.getSeatNumber());
            statement.setInt(4, reservedSeat.getClient().getId());
            statement.setTimestamp(5, Timestamp.valueOf(reservedSeat.getReservationDate()));
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                reservedSeat.setId(resultSet.getInt(1));
                return Optional.of(reservedSeat);
            }
        } catch (SQLException e) {
            logger.error("Database error while saving ReservedSeat", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReservedSeat> delete(Integer id) {
        logger.info("Delete ReservedSeat with ID: {}", id);
        Optional<ReservedSeat> reservedSeatToDelete = findById(id);
        if (reservedSeatToDelete.isEmpty()) {
            return Optional.empty();
        }

        String query = "DELETE FROM ReservedSeats WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return reservedSeatToDelete;
            }
        } catch (SQLException e) {
            logger.error("Database error while deleting ReservedSeat with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReservedSeat> update(ReservedSeat reservedSeat) {
        logger.info("Update ReservedSeat: {}", reservedSeat);
        String query = "UPDATE ReservedSeats SET trip_id=?, employee_id=?, seat_number=?, client_id=?, reservation_date=? WHERE id=?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, reservedSeat.getTrip().getId());
            statement.setInt(2, reservedSeat.getEmployee().getId());
            statement.setInt(3, reservedSeat.getSeatNumber());
            statement.setInt(4, reservedSeat.getClient().getId());
            statement.setTimestamp(5, Timestamp.valueOf(reservedSeat.getReservationDate()));
            statement.setInt(6, reservedSeat.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(reservedSeat);
            }
        } catch (SQLException e) {
            logger.error("Database error while updating ReservedSeat with id {}", reservedSeat.getId(), e);
        }
        return Optional.empty();
    }

    private Optional<ReservedSeat> extractReservedSeatFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        Integer tripId = resultSet.getInt("trip_id");
        int employeeId = resultSet.getInt("employee_id");
        Integer seatNumber = resultSet.getInt("seat_number");
        int clientId = resultSet.getInt("client_id");
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