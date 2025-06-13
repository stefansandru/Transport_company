using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using Avalonia.Data;
using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Logging;
using model;

namespace persistance;

public class ClientRepository : AbstractRepository<int, Client>, IClientRepository
{
    public ClientRepository() : base()
    {
        // Constructor specific for ClientRepository, if needed
    }

    public override Optional<Client> FindById(int id)
    {
        logger.LogInformation("Find Client by ID: {Id}", id);

        const string query = "SELECT * FROM Client WHERE id = @id";
        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                command.Parameters.AddWithValue("@id", id);
                connection.Open();

                using (var reader = command.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        var name = reader.GetString(reader.GetOrdinal("name"));
                        return new Optional<Client>(new Client(id, name));
                    }
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while finding Client with ID {Id}", id);
        }

        return new Optional<Client>();
    }

    public Optional<Client> FindByUsername(string username)
    {
        logger.LogInformation("Find Client by username: {Username}", username);

        if (string.IsNullOrEmpty(username))
            throw new ArgumentException("Username must not be null or empty", nameof(username));

        const string query = "SELECT * FROM Client WHERE name = @username";
        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                command.Parameters.AddWithValue("@username", username);
                connection.Open();

                using (var reader = command.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        var id = reader.GetInt32(reader.GetOrdinal("id"));
                        return new Optional<Client>(new Client(id, username));
                    }
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while finding Client with username {Username}", username);
        }

        return new Optional<Client>();
    }

    public Client FindByName(string name)
    {
        logger.LogInformation("Find Client by name: {Name}", name);

        if (string.IsNullOrEmpty(name))
            throw new ArgumentException("Name must not be null or empty", nameof(name));

        const string query = "SELECT * FROM Client WHERE name = @name";
        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                command.Parameters.AddWithValue("@name", name);
                connection.Open();

                using (var reader = command.ExecuteReader())
                {
                    if (reader.Read())
                    {
                        var id = reader.GetInt32(reader.GetOrdinal("id"));
                        return new Client(id, name);
                    }
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while finding Client with name {Name}", name);
        }

        return null;
    }

    public override IEnumerable<Client> FindAll()
    {
        logger.LogInformation("Find all Clients");

        var clients = new List<Client>();
        const string query = "SELECT * FROM Client";

        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                connection.Open();

                using (var reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        var id = reader.GetInt32(reader.GetOrdinal("id"));
                        var name = reader.GetString(reader.GetOrdinal("name"));
                        clients.Add(new Client(id, name));
                    }
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while finding all Clients");
        }

        return clients;
    }

    public override Optional<Client> Save(Client client)
        {
            logger.LogInformation("Save Client: {Client}", client);

            if (client == null)
                throw new ArgumentNullException(nameof(client));

            const string query = "INSERT INTO Client (name) VALUES (@name) RETURNING Id;";
            try
            {
                using (var connection = jdbc.GetConnection())
                using (var command = new SqliteCommand(query, (SqliteConnection)connection))
                {
                    command.Parameters.AddWithValue("@name", client.Name);
                    connection.Open();
                    var id = Convert.ToInt32(command.ExecuteScalar());
                    return new Optional<Client>(new Client(id, client.Name));
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while saving Client: {Client}", client);
            }

            return new Optional<Client>();
        }
    
    public override Optional<Client> Delete(int id)
    {
        logger.LogInformation("Delete Client with ID: {Id}", id);

        var clientToDelete = FindById(id);
        if (!clientToDelete.HasValue)
        {
            return new Optional<Client>();
        }

        const string query = "DELETE FROM Client WHERE id = @id";
        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                command.Parameters.AddWithValue("@id", id);
                connection.Open();
                var affectedRows = command.ExecuteNonQuery();

                if (affectedRows > 0)
                {
                    return clientToDelete;
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while deleting Client with ID {Id}", id);
        }

        return new Optional<Client>();
    }

    public override Optional<Client> Update(Client client)
    {
        logger.LogInformation("Update Client: {Client}", client);

        if (client == null)
            throw new ArgumentNullException(nameof(client));

        const string query = "UPDATE Client SET name = @name WHERE id = @id";
        try
        {
            using (var connection = jdbc.GetConnection())
            using (var command = new SqliteCommand(query, (SqliteConnection)connection))
            {
                command.Parameters.AddWithValue("@name", client.Name);
                command.Parameters.AddWithValue("@id", client.Id);
                connection.Open();
                var affectedRows = command.ExecuteNonQuery();

                if (affectedRows > 0)
                {
                    return new Optional<Client>(client);
                }
            }
        }
        catch (SqliteException e)
        {
            logger.LogError(e, "Database error while updating Client: {Client}", client);
        }

        return new Optional<Client>();
    }
}