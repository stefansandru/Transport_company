using System;
using System.Collections.Generic;
using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Logging;
using model;

namespace persistance
{
    public class ReservedSeatRepository : AbstractRepository<int, ReservedSeat>, IReservedSeatRepository
    {
        private readonly ITripRepository tripRepository;
        private readonly IEmployeeRepository employeeRepository;
        private readonly IClientRepository clientRepository;

        public ReservedSeatRepository(ITripRepository tripRepository, IEmployeeRepository employeeRepository, IClientRepository clientRepository) : base()
        {
            this.tripRepository = tripRepository;
            this.employeeRepository = employeeRepository;
            this.clientRepository = clientRepository;
        }

        private ReservedSeat? ExtractReservedSeatFromResultSet(SqliteDataReader reader)
        {
            try
            {
                var id = reader.GetInt32(reader.GetOrdinal("id"));
                var tripId = reader.GetInt32(reader.GetOrdinal("trip_id"));
                var employeeId = reader.GetInt32(reader.GetOrdinal("employee_id"));
                var seatNumber = reader.GetInt32(reader.GetOrdinal("seat_number"));
                var clientId = reader.GetInt32(reader.GetOrdinal("client_id"));

                var trip = tripRepository.FindById(tripId)
                    ?? throw new InvalidOperationException($"Trip with ID {tripId} not found");

                var reservedSeat = new ReservedSeat
                {
                    Id = id,
                    Trip = trip,
                    SeatNumber = seatNumber
                };

                if (employeeId != 0)
                {
                    var emp = employeeRepository.FindById(employeeId);
                    if (emp != null)
                    {
                        reservedSeat.Employee = emp;
                    }
                }
                if (clientId != 0)
                {
                    var client = clientRepository.FindById(clientId);
                    if (client != null)
                    {
                        reservedSeat.Client = client;
                    }
                }
                Console.WriteLine(reservedSeat);
                return reservedSeat;
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Error while extracting ReservedSeat from ResultSet");
                return null;
            }
        }

        public override ReservedSeat? FindById(int id)
        {
            logger.LogInformation("Find ReservedSeat by ID: {Id}", id);
            const string query = "SELECT * FROM ReservedSeats WHERE id = @id";
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
                            return ExtractReservedSeatFromResultSet(reader);
                        }
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while finding ReservedSeat with ID {Id}", id);
            }
            return null;
        }

        public override IEnumerable<ReservedSeat> FindAll()
        {
            logger.LogInformation("Find all ReservedSeats");
            var reservedSeats = new List<ReservedSeat>();
            const string query = "SELECT * FROM ReservedSeats";
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
                            var reservedSeat = ExtractReservedSeatFromResultSet(reader);
                            if (reservedSeat != null)
                            {
                                reservedSeats.Add(reservedSeat);
                            }
                        }
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while finding all ReservedSeats");
            }
            return reservedSeats;
        }

        public IEnumerable<ReservedSeat> FindByTripId(int tripId)
        {
            logger.LogInformation("Find ReservedSeats by Trip ID: {TripId}", tripId);
            var reservedSeats = new List<ReservedSeat>();
            const string query = "SELECT * FROM ReservedSeats WHERE trip_id = @trip_id";
            try
            {
                using (var connection = jdbc.GetConnection())
                using (var command = new SqliteCommand(query, (SqliteConnection)connection))
                {
                    command.Parameters.AddWithValue("@trip_id", tripId);
                    connection.Open();
                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            var reservedSeat = ExtractReservedSeatFromResultSet(reader);
                            if (reservedSeat != null)
                            {
                                reservedSeats.Add(reservedSeat);
                            }
                        }
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while finding ReservedSeats for trip ID {TripId}", tripId);
            }
            return reservedSeats;
        }

        public override ReservedSeat? Save(ReservedSeat reservedSeat)
        {
            logger.LogInformation("Save ReservedSeat: {ReservedSeat}", reservedSeat);
            const string query = "INSERT INTO ReservedSeats (trip_id, employee_id, seat_number, client_id) VALUES (@trip_id, @employee_id, @seat_number, @client_id)";
            try
            {
                using (var connection = jdbc.GetConnection())
                {
                    connection.Open();
                    using (var command = new SqliteCommand(query, (SqliteConnection)connection))
                    {
                        command.Parameters.AddWithValue("@trip_id", reservedSeat.Trip.Id);
                        command.Parameters.AddWithValue("@employee_id", reservedSeat.Employee.Id);
                        command.Parameters.AddWithValue("@seat_number", reservedSeat.SeatNumber);
                        command.Parameters.AddWithValue("@client_id", reservedSeat.Client.Id);
                        command.ExecuteNonQuery();
                    }
                    using (var idCommand = new SqliteCommand("SELECT last_insert_rowid()", (SqliteConnection)connection))
                    {
                        var id = Convert.ToInt32(idCommand.ExecuteScalar());
                        reservedSeat.Id = id;
                        return reservedSeat;
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while saving ReservedSeat: {ReservedSeat}", reservedSeat);
            }
            return null;
        }

        public override ReservedSeat? Delete(int id)
        {
            logger.LogInformation("Delete ReservedSeat with ID: {Id}", id);
            var reservedSeatToDelete = FindById(id);
            if (reservedSeatToDelete == null)
            {
                return null;
            }
            const string query = "DELETE FROM ReservedSeats WHERE id = @id";
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
                        return reservedSeatToDelete;
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while deleting ReservedSeat with ID {Id}", id);
            }
            return null;
        }

        public override ReservedSeat? Update(ReservedSeat reservedSeat)
        {
            logger.LogInformation("Update ReservedSeat: {ReservedSeat}", reservedSeat);
            const string query = "UPDATE ReservedSeats SET trip_id = @trip_id, employee_id = @employee_id, seat_number = @seat_number, client_id = @client_id WHERE id = @id";
            try
            {
                using (var connection = jdbc.GetConnection())
                using (var command = new SqliteCommand(query, (SqliteConnection)connection))
                {
                    command.Parameters.AddWithValue("@trip_id", reservedSeat.Trip.Id);
                    command.Parameters.AddWithValue("@employee_id", reservedSeat.Employee.Id);
                    command.Parameters.AddWithValue("@seat_number", reservedSeat.SeatNumber);
                    command.Parameters.AddWithValue("@client_id", reservedSeat.Client.Id);
                    command.Parameters.AddWithValue("@id", reservedSeat.Id);
                    connection.Open();
                    var affectedRows = command.ExecuteNonQuery();
                    if (affectedRows > 0)
                    {
                        return reservedSeat;
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while updating ReservedSeat: {ReservedSeat}", reservedSeat);
            }
            return null;
        }

        public List<ReservedSeat> FindByTripDestinationDateTime(string destination, string date, string time)
        {
            logger.LogInformation("Find ReservedSeat by trip destination, date and time: {Destination}: {Date}, {Time}", destination, date, time);
            var reservedSeats = new List<ReservedSeat>();
            const string query = @"
        SELECT * FROM ReservedSeats rs
        JOIN Trip t ON rs.trip_id = t.id
        JOIN Destination d ON t.destination_id = d.id
        WHERE d.name = @destination AND t.departure_date = @date AND t.departure_time = @time";
            try
            {
                using (var connection = jdbc.GetConnection())
                using (var command = new SqliteCommand(query, (SqliteConnection)connection))
                {
                    command.Parameters.AddWithValue("@destination", destination);
                    command.Parameters.AddWithValue("@date", date);
                    command.Parameters.AddWithValue("@time", time);
                    connection.Open();
                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            var reservedSeat = ExtractReservedSeatFromResultSet(reader);
                            if (reservedSeat != null)
                            {
                                reservedSeats.Add(reservedSeat);
                            }
                        }
                    }
                }
            }
            catch (SqliteException e)
            {
                logger.LogError(e, "Database error while finding ReservedSeat by trip destination, date and time: {Destination}: {Date}, {Time}", destination, date, time);
            }
            return reservedSeats;
        }
    }
}