using model;
using networking.dto;

namespace networking.jsonprotocol;

public class Response
{
    public ResponseType? ResponseType { get; set; }
    public string? ErrorMessage { get; set; }
    public EmployeeDTO? LoggedInEmployeeDTO { get; set; }
    public TripDTO? TripDTO { get; set; }
    public DTOUtils? DtoUtils { get; set; }
    public List<Trip>? Trips { get; set; }
    public List<SeatDTO>? Seats { get; set; }
    public Employee? LoggedEmployee { get; set; }
    public Trip? Trip { get; set; }

    public override string ToString()
    {
        return $"Response{{ResponseType={ResponseType}, ErrorMessage='{ErrorMessage}', LoggedEmployee={LoggedEmployee}, ReservedTrip={Trip}}}";
    }

}