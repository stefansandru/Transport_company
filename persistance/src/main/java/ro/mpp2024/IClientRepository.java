package ro.mpp2024;


import java.util.Optional;

public interface IClientRepository extends IRepository<Integer, Client> {

    /**
     *
     * @param username - the username of the client to be returned
     * @return an {@code Optional} encapsulating the client with the given username
     * @throws IllegalArgumentException if username is null
     */
    Optional<Client> findByUsername(String username);
}
