package ro.mpp2024;


public interface IDestinationRepository extends IRepository<Integer, Destination> {

    /**
     *
     * @param name - the name of the destination to be returned
     * @return an {@code Optional} encapsulating the destination with the given name
     * @throws IllegalArgumentException if name is null
     */
    Iterable<Destination> findByName(String name);
}
