package ro.mpp2024.repository;

import ro.mpp2024.model.Entity;
import ro.mpp2024.utils.JdbcUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Properties;

public abstract class AbstractRepository<ID extends Comparable<ID>, E extends Entity<ID>> implements  IRepository<ID, E> {
    protected JdbcUtils jdbc;
//    protected static final Logger logger = LogManager.getLogger(AbstractRepository.class);
    protected static final Logger logger= LogManager.getLogger();


    // Default constructor that loads properties automatically
//    public AbstractRepository() {
//        this(dbUtils.loadProperties("database.properties"));
//    }

    public AbstractRepository(Properties props) {
        jdbc=new JdbcUtils(props);
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
