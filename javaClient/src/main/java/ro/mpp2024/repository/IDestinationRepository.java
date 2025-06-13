package ro.mpp2024.repository;

import ro.mpp2024.model.Destination;

import java.util.Optional;

public interface IDestinationRepository extends IRepository<Integer, Destination> {

    /**
     *
     * @param name - the name of the destination to be returned
     * @return an {@code Optional} encapsulating the destination with the given name
     * @throws IllegalArgumentException if name is null
     */
    Iterable<Destination> findByName(String name);
}
