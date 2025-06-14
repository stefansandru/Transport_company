using System.Collections.Concurrent;
using Grpc.Core;
using TransportCompany.GrpcServer;
using model;
using persistance;
using Microsoft.Extensions.Logging;

namespace grpcServer;

public class TransportCompanyService : TransportCompany.GrpcServer.TransportCompany.TransportCompanyBase
{
    private readonly IClientRepository _clientRepository;
    private readonly IEmployeeRepository _employeeRepository;
    private readonly ITripRepository _tripRepository;
    private readonly IReservedSeatRepository _reservedSeatRepository;
    private readonly ILogger<TransportCompanyService> _logger;

    // Lista employeeId -> bool pentru login unic
    private static readonly ConcurrentDictionary<int, bool> LoggedInEmployees = new();

    // Lista employeeId -> stream pentru notificări (streaming)
    private static readonly ConcurrentDictionary<int, IServerStreamWriter<NotifySeatsReservedReply>> EmployeeStreams = new();

    public TransportCompanyService(
        IClientRepository clientRepository,
        IEmployeeRepository employeeRepository, 
        ITripRepository tripRepository,
        IReservedSeatRepository reservedSeatRepository,
        ILogger<TransportCompanyService> logger)
    {
        _clientRepository = clientRepository;
        _employeeRepository = employeeRepository;
        _tripRepository = tripRepository;
        _reservedSeatRepository = reservedSeatRepository;
        _logger = logger;
    }  

    public override Task<LoginReply> Login(LoginRequest request, ServerCallContext context)
    {
        var employee = _employeeRepository.FindByUsername(request.Username);

        if (employee == null)
        {
            _logger.LogWarning("Employee not found!");
            return Task.FromResult(new LoginReply { EmployeeId = -1, Username = "INVALID" });
        }

        // NU PERMITE LOGIN DUBLU
        if (LoggedInEmployees.ContainsKey(employee.Id))
        {
            _logger.LogWarning("Employee already logged in!");
            return Task.FromResult(new LoginReply { EmployeeId = -3, Username = "ALREADY_LOGGED" });
        }

        if (!BCrypt.Net.BCrypt.Verify(request.Password, employee.Password))
        {
            _logger.LogWarning("Invalid password!");
            return Task.FromResult(new LoginReply { EmployeeId = -2, Username = "INVALID" });
        }
        
        LoggedInEmployees[employee.Id] = true;
        _logger.LogInformation($"Login reușit pentru {employee.Username} ");

        return Task.FromResult(new LoginReply
        {
            EmployeeId = employee.Id,
            Username = employee.Username,
        });
    }

    public override Task<LogoutReply> Logout(LogoutRequest request, ServerCallContext context)
    {
        LoggedInEmployees.TryRemove(request.EmployeeId, out _);
        EmployeeStreams.TryRemove(request.EmployeeId, out _);
        _logger.LogInformation($"Logout pentru employeeId={request.EmployeeId}");

        return Task.FromResult(new LogoutReply
        {
            Success = true,
            Message = "Logged out successfully"
        });
    }

    public override Task<TripsReply> GetAllTrips(AllTripsRequest request, ServerCallContext context)
    {
        var trips = _tripRepository.FindAll().ToList();
        var reply = new TripsReply();
        foreach (var trip in trips)
        {
            var protoTrip = new TransportCompany.GrpcServer.TripDTO
            {
                Id = trip.Id,
                Destination = trip.Destination.Name,
                Date = trip.DepartureDate.ToString("yyyy-MM-dd"),
                Time = trip.DepartureTime.ToString(@"HH\:mm"),
                AvailableSeats = trip.AvailableSeats ?? 0
            };
            reply.Trips.Add(protoTrip);
        }
        return Task.FromResult(reply);
    }

    public override Task<GetTripReply> GetTrip(GetTripRequest request, ServerCallContext context)
    {
        if (!DateOnly.TryParse(request.Date, out var date))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid date format"));
        if (!TimeOnly.TryParse(request.Time, out var time))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid time format"));

        var trip = _tripRepository.FindByDestinationAndDateAndTime(request.Destination, request.Date, request.Time);
        _logger.LogInformation("Trip found:");
        _logger.LogInformation(trip?.ToString());

        if (trip == null)
            return Task.FromResult(new GetTripReply());

        var tripDto = new TripDTO
        {
            Id = trip.Id,
            Destination = trip.Destination.Name,
            Date = trip.DepartureDate.ToString("yyyy-MM-dd"),
            Time = trip.DepartureTime.ToString(@"HH\:mm"),
            AvailableSeats = trip.AvailableSeats ?? 0
        };

        return Task.FromResult(new GetTripReply { Trip = tripDto });
    }

    public override Task<SearchTripSeatsReply> SearchTripSeats(SearchTripSeatsRequest request, ServerCallContext context)
    {
        if (!DateOnly.TryParse(request.Date, out var date))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid date format"));
        if (!TimeOnly.TryParse(request.Time, out var time))
            throw new RpcException(new Status(StatusCode.InvalidArgument, "Invalid time format"));

        if (_reservedSeatRepository == null)
            throw new NotSupportedException("ReservedSeatRepository not available");

        var reservedSeats = _reservedSeatRepository.FindByTripDestinationDateTime(
            request.Destination, request.Date, request.Time);

        var reply = new SearchTripSeatsReply();

        for (int seat = 1; seat <= 18; seat++)
        {
            string clientName = reservedSeats.FirstOrDefault(s => s.SeatNumber == seat)?.Client?.Name ?? "-";
            reply.Seats.Add(new SeatDTODTO
            {
                SeatNumber = seat,
                ClientName = clientName
            });
        }

        return Task.FromResult(reply);
    }

    public override async Task<ReserveSeatsReply> ReserveSeats(ReserveSeatsRequest request, ServerCallContext context)
    {
        var client = _clientRepository.FindByName(request.ClientName);
        if (client == null)
        {
            client = new Client { Name = request.ClientName };
            client = _clientRepository.Save(client);
        }

        var trip = _tripRepository.FindById(request.Trip.Id);
        if (trip == null)
            throw new RpcException(new Status(StatusCode.NotFound, "Trip not found"));

        var employee = _employeeRepository.FindById(request.EmployeeId);
        if (employee == null)
            throw new RpcException(new Status(StatusCode.NotFound, "Employee not found"));

        foreach (var seatNumber in request.SeatNumbers)
        {
            var reservedSeat = new ReservedSeat
            {
                Trip = trip,
                Employee = employee,
                SeatNumber = seatNumber,
                Client = client
            };
            _reservedSeatRepository.Save(reservedSeat);
        }

        trip.AvailableSeats = (trip.AvailableSeats ?? 0) - request.SeatNumbers.Count;
        // _tripRepository.Update(trip);

        // NOTIFICĂ TOȚI CLIENTII LOGAȚI (mai puțin pe cel care rezervă acum)
        foreach (var entry in EmployeeStreams)
        {
            if (entry.Key != employee.Id)
            {
                try
                {
                    await entry.Value.WriteAsync(new NotifySeatsReservedReply());
                }
                catch
                {
                    // Clientul a deconectat, îl scoatem
                    EmployeeStreams.TryRemove(entry.Key, out _);
                }
            }
        }

        var reply = new ReserveSeatsReply
        {
            Success = true,
            Message = $"Reserved {request.SeatNumbers.Count} seat(s) for {client.Name} on trip {trip.Id}"
        };

        return reply;
    }

    // RPC STREAMING - fiecare client care vrea notificări apelează metoda asta
    public override async Task NotifySeatsReserved(NotifySeatsReservedRequest request, IServerStreamWriter<NotifySeatsReservedReply> responseStream, ServerCallContext context)
    {
        // Înregistrează stream (un singur stream per employeeId)
        EmployeeStreams[request.EmployeeId] = responseStream; // Folosește request.TripId sau employeeId în funcție de cum e definit mesajul!
        try
        {
            // Ține "viu" stream-ul, altfel se închide și dezactivează notificarea pentru acest client
            while (!context.CancellationToken.IsCancellationRequested)
            {
                await Task.Delay(1000);
            }
        }
        finally
        {
            EmployeeStreams.TryRemove(request.EmployeeId, out _);
        }
    }
}