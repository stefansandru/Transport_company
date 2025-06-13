package ro.mpp2024;


import java.util.Optional;

public interface IEmployeeRepository extends  IRepository<Integer, Employee>{

    /**
     *
     * @param username - the username of the employee to be returned
     * @return an {@code Optional} encapsulating the employee with the given username
     * @throws IllegalArgumentException if username is null
     */
    Optional<Employee> findByUsername(String username);
}
