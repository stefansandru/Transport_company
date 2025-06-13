using System;
using Avalonia.Data;
using model;

namespace persistance;

public interface IOfficeRepository : IRepository<int, Office>
{
    /// <summary>
    /// Finds an office by its name.
    /// </summary>
    /// <param name="name">The name of the office to be returned.</param>
    /// <returns>An <see cref="Optional{T}"/> encapsulating the office with the given name.</returns>
    /// <exception cref="ArgumentException">Thrown if <paramref name="name"/> is null.</exception>
    Optional<Office> FindByName(string name);
}