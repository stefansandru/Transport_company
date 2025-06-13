using System;
using System.Collections.Generic;
using Avalonia.Data;
using Microsoft.Extensions.Logging;
using model;

namespace persistance;

public abstract class AbstractRepository<ID, E> : IRepository<ID, E> where ID : IComparable<ID> where E : Entity<ID>
{
    protected JdbcUtils jdbc;
    protected static readonly ILogger logger = LoggerFactory.Create(builder => builder.AddConsole()).CreateLogger<AbstractRepository<ID, E>>();

    protected AbstractRepository()
    {
        // Initialize JdbcUtils with configuration from appsettings.json
        jdbc = new JdbcUtils();
    }

    public abstract Optional<E> FindById(ID id);

    public abstract IEnumerable<E> FindAll();
    
    public abstract Optional<E> Save(E entity);

    public abstract Optional<E> Delete(ID id);

    public abstract Optional<E> Update(E entity);
}