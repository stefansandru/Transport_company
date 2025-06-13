using System.Collections.Concurrent;
using Avalonia.Data;
using log4net;
using model;
using persistance;
using services;

namespace server;

public class TaskManagementSystemServicesImpl : ITaskManagementServices
{
    private readonly IClientRepository clientRepository;
    private readonly IEmployeeRepository employeeRepository;
    private readonly IReservedSeatRepository reservedSeatRepository;
    private readonly ITripRepository tripRepository;

    private readonly IDictionary<int, IObserver> loggedEmployees;
    private static readonly ILog log = LogManager.GetLogger(typeof(TaskManagementSystemServicesImpl));

    public TaskManagementSystemServicesImpl(
        IClientRepository clientRepository,
        IEmployeeRepository employeeRepository,
        IReservedSeatRepository reservedSeatRepository,
        ITripRepository tripRepository)
    {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.reservedSeatRepository = reservedSeatRepository;
        this.tripRepository = tripRepository;
        loggedEmployees = new ConcurrentDictionary<int, IObserver>();
    }

    public Employee Login(string username, string password, IObserver client)
    {
        string hashedPassword = CryptPassword.HashPassword("bob");
        Console.WriteLine(hashedPassword);
        
        log.Debug("Entering Login");
        Console.WriteLine("entering Login");
        var employee = employeeRepository.FindByUsername(username);
        if (employee == null)
        {
            log.Info($"Employee with username {username} not found");
            throw new ServicesException("Invalid usernameeeee");
        }

        if (loggedEmployees.ContainsKey(employee.Id))
        {
            throw new ServicesException("Employee already logged in.");
        }

        if (!CryptPassword.CheckPassword(password, employee.Password))
        {
            log.Info($"Invalid password for employee {username}");
            {
                throw new ServicesException("Invalid password");
            }
        }
        loggedEmployees[employee.Id] = client;
        return employee;
    }

    public void Logout(Employee employee)
    {
        if (employee == null) // || employee.Id == null)
            throw new ServicesException("Invalid employee.");
        log.Debug($"Logging out: {employee.Username}");
        bool removed = loggedEmployees.Remove(employee.Id);
        if (!removed)
            throw new ServicesException($"{employee} is not logged in.");
    }

    public List<Trip> GetAllTrips()
    {
        return tripRepository.FindAll().ToList();
    }

    public List<SeatDTO> SearchTripSeats(string destination, DateOnly date, TimeOnly time)
    {
        Console.WriteLine($"Entering SearchTripSeats: {destination} {date.ToShortDateString()} {time}");
        string dateString = date.ToString("yyyy-MM-dd");
        string timeString = time.ToString(@"HH\:mm");

        var reservedSeats =
            reservedSeatRepository.FindByTripDestinationDateTime(destination, dateString, timeString);
        var allSeats = new List<SeatDTO>();

        for (int seat = 1; seat <= 18; seat++)
        {
            string clientName = reservedSeats
                .FirstOrDefault(s => s.SeatNumber == seat)?.Client?.Name ?? "-";
            allSeats.Add(new SeatDTO(seat, clientName));
            Console.WriteLine(new SeatDTO(seat, clientName));;
        }

        return allSeats;
    }

    public void ReserveSeats(string clientName, List<int> seatNumbers, Trip trip, Employee employee)
    {
        log.Debug("Entering ReserveSeats");

        // Save or retrieve the client
        var client = clientRepository.FindByName(clientName);
        if (client == null)
        {
            client = new Client { Name = clientName };
            client = clientRepository.Save(client).Value;
        }

        // Save reserved seats
        foreach (var seatNumber in seatNumbers)
        {
            var reservedSeat = new ReservedSeat
            {
                Trip = trip,
                Employee = employee,
                SeatNumber = seatNumber,
                Client = client
            };
            reservedSeatRepository.Save(reservedSeat);
        }

        // Update available seats
        trip.AvailableSeats -= seatNumbers.Count;
        Console.WriteLine(trip);
        tripRepository.Update(trip);
        Console.WriteLine($"Updated trip {trip.Id} with available seats: {trip.AvailableSeats}");

        // Notify other employees
        foreach (var entry in loggedEmployees)
        {
            if (entry.Key != (employee.Id))
            {
                var observer = entry.Value;
                Task.Run(() =>
                {
                    try
                    {
                        log.Debug($"Notifying employee ID {entry.Key} (seats reserved)");
                        observer.SeatsReserved();
                    }
                    catch (ServicesException ex)
                    {
                        log.Error($"Error notifying about reservation: {ex.Message}");
                    }
                });
            }
        }
    }

    public Trip GetTrip(string destination, DateOnly date, TimeOnly time)
    {
        log.Debug("Entering GetTrip");
        string dateString = date.ToString("yyyy-MM-dd");
        string timeString = time.ToString(@"hh\:mm");

        var trip = tripRepository.FindByDestinationAndDateAndTime(destination, dateString, timeString);
        if (trip == null)
        {
            log.Warn("Trip not found");
        }

        return trip;
    }
}
