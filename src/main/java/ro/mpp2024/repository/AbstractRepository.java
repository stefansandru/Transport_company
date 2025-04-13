package ro.mpp2024.repository;

import ro.mpp2024.model.Entity;
import ro.mpp2024.utils.JdbcUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.Properties;

public abstract class AbstractRepository<ID extends Comparable<ID>, E extends Entity<ID>> implements  IRepository<ID, E> {
    protected JdbcUtils jdbc;
    protected static final Logger logger= LogManager.getLogger();

    public AbstractRepository(Properties props) {
        jdbc=new JdbcUtils(props);
    }

    @Override
    public abstract Optional<E> findById(ID id);

    @Override
    public abstract Iterable<E> findAll();

    @Override
    public abstract Optional<E> delete(ID id);

    @Override
    public abstract Optional<E> update(E entity);
}
