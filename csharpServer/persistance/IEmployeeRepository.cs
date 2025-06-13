using System;
using Avalonia.Data;
using model;

namespace persistance;

public interface IEmployeeRepository : IRepository<int, Employee>
{
    /// <summary>
    /// Finds an employee by their username and password.
    /// </summary>
    /// <param name="username">The username of the employee to be returned.</param>
    /// <returns>An <see cref="Optional{T}"/> encapsulating the employee with the given username and password.</returns>
    /// <exception cref="ArgumentException">Thrown if <paramref name="username"/>  is null.</exception>
    Employee FindByUsername(string username);
}