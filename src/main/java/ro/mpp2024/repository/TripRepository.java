package ro.mpp2024.repository;

import ro.mpp2024.model.Trip;
import ro.mpp2024.model.Destination;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TripRepository {

    private final Logger logger = LoggerFactory.getLogger(TripRepository.class);

    private final String url;
    private final String user;
    private final String password;
    private final DestinationRepository destinationRepository;

    public TripRepository(
            String url,
            String user,
            String password,
            DestinationRepository destinationRepository) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.destinationRepository = destinationRepository;
    }

    private Optional<Trip> extractTripFromResultSet(ResultSet resultSet) {
        try {
            Integer id = resultSet.getInt("id");
            Integer destinationId = resultSet.getInt("destination_id");
            LocalDate departureDate = resultSet.getDate("departure_date").toLocalDate();
            LocalTime departureTime = resultSet.getTime("departure_time").toLocalTime();
            Integer availableSeats = resultSet.getInt("available_seats");

            return destinationRepository.findById(destinationId).map(
                    destination -> new Trip(
                            id,
                            destination,
                            departureDate,
                            departureTime,
                            availableSeats));
        } catch (SQLException e) {
            logger.error("Error while extracting Trip from ResultSet", e);
            return Optional.empty();
        }
    }

    public Optional<Trip> findById(Integer id) {
        String query = "SELECT * FROM Trip WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return extractTripFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Trip with id {}", id, e);
        }
        return Optional.empty();
    }

    public List<Trip> findAll() {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                extractTripFromResultSet(resultSet).ifPresent(trips::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Trips", e);
        }
        return trips;
    }

    public List<Trip> findByDestinationId(Integer destinationId) {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip WHERE destination_id = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, destinationId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                extractTripFromResultSet(resultSet).ifPresent(trips::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Trips for destination id {}", destinationId, e);
        }
        return trips;
    }

    public List<Trip> findByDepartureDate(LocalDate departureDate) {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip WHERE departure_date = ?";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(departureDate));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                extractTripFromResultSet(resultSet).ifPresent(trips::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Trips for departure date {}", departureDate, e);
        }
        return trips;
    }

    public boolean save(Trip trip) {
        String query = "INSERT INTO Trip(destination_id, departure_date, departure_time, available_seats) VALUES(?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, trip.getDestination().getId());
            statement.setDate(2, Date.valueOf(trip.getDepartureDate()));
            statement.setTime(3, Time.valueOf(trip.getDepartureTime()));
            statement.setInt(4, trip.getAvailableSeats());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while saving Trip", e);
        }
        return false;
    }
}