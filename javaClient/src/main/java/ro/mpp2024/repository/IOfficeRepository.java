package ro.mpp2024.repository;

import ro.mpp2024.model.Office;

import java.util.Optional;

public interface IOfficeRepository extends IRepository<Integer, Office> {

    /**
     *
     * @param name - the name of the office to be returned
     * @return an {@code Optional} encapsulating the office with the given name
     * @throws IllegalArgumentException if name is null
     */
    Optional<Office> findByName(String name);
}
