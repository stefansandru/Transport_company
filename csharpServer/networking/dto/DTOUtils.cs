using model;

namespace networking.dto;

public class DTOUtils
{
    public static EmployeeDTO GetEmployeeDTO(Employee employee)
    {
        return new EmployeeDTO(employee.Id, employee.Username, employee.Password, employee.Office.Id);
    }

    // public static Employee GetEmployee(EmployeeDTO employeeDTO)
    // {
    //     Office office = new Office(employeeDTO.OfficeId ?? 0, null);
    //     return new Employee(employeeDTO.Id, employeeDTO.Username, employeeDTO.Password, office);
    // }

    public static TripDTO GetTripDTO(Trip trip)
    {
        return new TripDTO(
            trip.Id,
            trip.Destination.Id,
            trip.DepartureDate,
            trip.DepartureTime,
            trip.AvailableSeats
        );
    }

    public static Trip GetTrip(TripDTO tripDTO)
    {
        Destination destination = new Destination(tripDTO.DestinationId, null);
        return new Trip(
            tripDTO.Id,
            destination,
            tripDTO.DepartureDate,
            tripDTO.DepartureTime,
            tripDTO.AvailableSeats
        );
    }
}