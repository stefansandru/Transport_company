using System;
using Avalonia.Data;
using model;
using model;

namespace persistance;

public interface IClientRepository : IRepository<int, Client>
{
    /// <summary>
    /// Finds a client by their username.
    /// </summary>
    /// <param name="username">The username of the client to be returned.</param>
    /// <returns>An <see cref="Optional{T}"/> encapsulating the client with the given username.</returns>
    /// <exception cref="ArgumentException">Thrown if <paramref name="username"/> is null.</exception>
    Client FindByName(string username);
}