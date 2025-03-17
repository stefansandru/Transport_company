package ro.mpp2024.repository;

import java.util.Optional;


/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface IRepository<ID, E> {

    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return an {@code Optional} encapsulating the entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    Optional<E> findById(ID id);

    /**
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     *
     * @param entity
     *         entity must be not null
     * @return an {@code Optional} - the entity, if it was saved,
     *                             - null, otherwise (id already exists)
     * @throws IllegalArgumentException
     *             if the given entity is null.     *
     */
    Optional<E> save(E entity);


    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return an {@code Optional}
     *            - null if there is no entity with the given id,
     *            - the removed entity, otherwise
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    Optional<E> delete(ID id);

    /**
     *
     * @param entity
     *          entity must not be null
     * @return  an {@code Optional}
     *             - the entity, if it was updated
     *             - null, otherwise (e.g. id does not exist)
     * @throws IllegalArgumentException
     *             if the given entity is null.
     */
    Optional<E> update(E entity);
}


