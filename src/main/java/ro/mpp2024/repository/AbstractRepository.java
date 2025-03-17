package ro.mpp2024.repository;

import ro.mpp2024.model.Entity;
import ro.mpp2024.utils.Jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Properties;

public abstract class AbstractRepository<ID extends Comparable<ID>, E extends Entity<ID>> implements  IRepository<ID, E> {
    protected final Jdbc jdbc;
    protected static final Logger logger = LogManager.getLogger(AbstractRepository.class);

    public AbstractRepository(Properties props) {
        logger.info("AbstractDBRepository constructor with props: {} ", props);
        jdbc = new Jdbc(props);
    }

    @Override
    public Optional<E> findById(ID id) {
        return Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        return null;
    }

    @Override
    public Optional<E> save(E entity) {
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) {
        return Optional.empty();
    }
}
