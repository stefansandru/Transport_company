using model;
using networking.dto;

namespace networking.jsonprotocol;

public class Request
{
    public RequestType? RequestType { get; set; }
    public string? Username { get; set; }
    public string? Password { get; set; }
    public EmployeeDTO? EmployeeDTO { get; set; }
    public TripDTO? TripDTO { get; set; }
    public DTOUtils? DtoUtils { get; set; }
    public List<Trip>? Trips { get; set; }
    public List<SeatDTO>? Seats { get; set; }
    public Employee? CurentEmployee { get; set; }
    public Trip? TripToReserve { get; set; }
    public string? ClientName { get; set; }
    public List<int>? SeatsNumbers { get; set; }
    public string? TripDestination { get; set; }
    public DateOnly TripDate { get; set; }
    public TimeOnly TripTime { get; set; }
    public string? Destination { get; set; }

    public override string ToString()
    {
        return $"Request{{RequestType={RequestType}, EmployeeDTO={EmployeeDTO}, TripDTO={TripDTO}}}";
    }

}