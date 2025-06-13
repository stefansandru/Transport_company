package ro.mpp2024.repository;

import ro.mpp2024.model.Trip;

import java.util.Optional;

/**
 * Interface for Trip repository.
 * Extends the generic IRepository interface with Integer as the ID type and Trip as the entity type.
 */
public interface ITripRepository extends IRepository<Integer, Trip> {

    /**
     * Finds all trips by the given destination name.
     *
     * @param name - the name of the destination to be returned
     * @return an {@code Iterable} encapsulating the trips with the given destination name
     * @throws IllegalArgumentException if name is null
     */
    Iterable<Trip> findAllByName(String name);

    /**
     * Finds trip by destination, date, and time.
     *
     * @param destination - the destination of the trip
     * @param date - the date of the trip
     * @param time - the time of the trip
     * @return a {@code List} of trips matching the given destination, date, and time
     */
    Optional<Trip> findByDestinationAndDateAndTime(String destination, String date, String time);
}