namespace networking.dto;

public class TripDTO
{
    public int Id { get; set; }
    public int DestinationId { get; set; }
    public DateOnly DepartureDate { get; set; }
    public TimeOnly DepartureTime { get; set; }
    public int? AvailableSeats { get; set; }

    public TripDTO(
        int id,
        int destinationId,
        DateOnly departureDate,
        TimeOnly departureTime,
        int? availableSeats)
    {
        Id = id;
        DestinationId = destinationId;
        DepartureDate = departureDate;
        DepartureTime = departureTime;
        AvailableSeats = availableSeats;
    }

    public override string ToString()
    {
        return $"TripDTO{{id={Id}, destinationId={DestinationId}, departureDate={DepartureDate.ToShortDateString()}, departureTime={DepartureTime}, availableSeats={AvailableSeats}}}";
    }
}