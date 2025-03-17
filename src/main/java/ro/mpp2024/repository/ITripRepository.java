package ro.mpp2024.repository;

import ro.mpp2024.model.Trip;

public interface ITripRepository extends IRepository<Integer, Trip> {

    /**
     *
     * @param name - the id of the destination to be returned
     * @return an {@code Iterable} encapsulating the trips with the given destination id
     * @throws IllegalArgumentException if destinationId is null
     */
    Iterable<Trip> findAllByName(String name);
}
