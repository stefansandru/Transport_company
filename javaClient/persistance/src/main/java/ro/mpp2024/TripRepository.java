package ro.mpp2024;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class TripRepository extends AbstractRepository<Integer, Trip> implements ITripRepository {

    private final DestinationRepository destinationRepository;

    public TripRepository(Properties properties, DestinationRepository destinationRepository) {
        super(properties);
        this.destinationRepository = destinationRepository;
    }

//    private Optional<Trip> extractTripFromResultSet(ResultSet resultSet) {
//        try {
//            Integer id = resultSet.getInt("id");
//            Integer destinationId = resultSet.getInt("destination_id");
//            LocalDate departureDate = resultSet.getDate("departure_date").toLocalDate();
//            LocalTime departureTime = resultSet.getTime("departure_time").toLocalTime();
//            Integer availableSeats = resultSet.getInt("available_seats");
//
//            return destinationRepository.findById(destinationId).map(
//                    destination -> new Trip(
//                            id,
//                            destination,
//                            departureDate,
//                            departureTime,
//                            availableSeats));
//        } catch (SQLException e) {
//            logger.error("Error while extracting Trip from ResultSet", e);
//            return Optional.empty();
//        }
//    }

    private Optional<Trip> extractTripFromResultSet(ResultSet resultSet) {
        try {
            Integer id = resultSet.getInt("id");
            Integer destinationId = resultSet.getInt("destination_id");

            // Get date as string and parse it
            String departureDateStr = resultSet.getString("departure_date");
            if (departureDateStr == null) {
                logger.error("departure_date is null for Trip ID: {}", id);
                return Optional.empty();
            }
            LocalDate departureDate = LocalDate.parse(departureDateStr);

            // Get time as string and parse it
            String departureTimeStr = resultSet.getString("departure_time");
            if (departureTimeStr == null) {
                logger.error("departure_time is null for Trip ID: {}", id);
                return Optional.empty();
            }
            LocalTime departureTime = LocalTime.parse(departureTimeStr);

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

    @Override
    public Optional<Trip> findById(Integer id) {
        logger.info("Find Trip by ID: {}", id);
        String query = "SELECT * FROM Trip WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
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

    @Override
    public List<Trip> findAll() {
        logger.info("Find all Trips");
        List<Trip> trips = new ArrayList<>();
   String query = "select * from Trip";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find Trips by Destination ID: {}", destinationId);
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip WHERE destination_id = ?";
        try (Connection connection = jdbc.getConnection();
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
        logger.info("Find Trips by Departure Date: {}", departureDate);
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip WHERE departure_date = ?";
        try (Connection connection = jdbc.getConnection();
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

    @Override
    public Optional<Trip> save(Trip trip) {
        logger.info("Save Trip: {}", trip);
        String query = "INSERT INTO Trip(destination_id, departure_date, departure_time, available_seats) VALUES(?, ?, ?, ?)";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, trip.getDestination().getId());
            statement.setString(2, trip.getDepartureDate().toString());  // Use toString() instead of Date.valueOf()
            statement.setString(3, trip.getDepartureTime().toString());  // Use toString() instead of Time.valueOf()
            statement.setInt(4, trip.getAvailableSeats());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setId(generatedKeys.getInt(1));
                    return Optional.of(trip);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while saving Trip", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Trip> delete(Integer id) {
        logger.info("Delete Trip with ID: {}", id);
        Optional<Trip> tripToDelete = findById(id);
        if (tripToDelete.isEmpty()) {
            return Optional.empty();
        }

        String query = "DELETE FROM Trip WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return tripToDelete;
            }
        } catch (SQLException e) {
            logger.error("Database error while deleting Trip with id {}", id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Trip> update(Trip trip) {
        logger.info("Update Trip: {}", trip);
        String query = "UPDATE Trip SET destination_id = ?, departure_date = ?, departure_time = ?, available_seats = ? WHERE id = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, trip.getDestination().getId());
            statement.setString(2, trip.getDepartureDate().toString());  // Use toString() instead of Date.valueOf()
            statement.setString(3, trip.getDepartureTime().toString());  // Use toString() instead of Time.valueOf()
            statement.setInt(4, trip.getAvailableSeats());
            statement.setInt(5, trip.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                return Optional.of(trip);
            }
        } catch (SQLException e) {
            logger.error("Database error while updating Trip", e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Trip> findAllByName(String name) {
        logger.info("Find all Trips by name: {}", name);
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT * FROM Trip t JOIN Destination d ON t.destination_id = d.id WHERE d.name = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                extractTripFromResultSet(resultSet).ifPresent(trips::add);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding all Trips by name", e);
        }
        return trips;
    }

    @Override
    public Optional<Trip> findByDestinationAndDateAndTime(String destination, String date, String time) {
        logger.info("Find Trip by destination {} and date: {}, time: {}", destination, date, time);
        String query = "SELECT * FROM Trip t JOIN Destination d ON t.destination_id = d.id WHERE d.name = ? AND t.departure_date = ? AND t.departure_time = ?";
        try (Connection connection = jdbc.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, destination);
            statement.setString(2, date);
            statement.setString(3, time);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return extractTripFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Database error while finding Trip by destination {} and date: {}, time: {}", destination, date, time, e);
        }
        return Optional.empty();
    }
}