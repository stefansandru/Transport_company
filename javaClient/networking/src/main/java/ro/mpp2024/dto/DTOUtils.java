package ro.mpp2024.dto;

import ro.mpp2024.Employee;
import ro.mpp2024.Office;
import ro.mpp2024.Trip;
import ro.mpp2024.Destination;

public class DTOUtils {
    public static EmployeeDTO getEmployeeDTO(Employee employee) {
        return new EmployeeDTO(employee.getId(), employee.getUsername(), employee.getPassword(), employee.getOffice().getId());
    }

    public static Employee getEmployee(EmployeeDTO employeeDTO) {
        Office office = new Office(employeeDTO.getOfficeId(), null);
        return new Employee(employeeDTO.getId(), employeeDTO.getUsername(), employeeDTO.getPassword(), office);
    }

    public static TripDTO getTripDTO(Trip trip) {
        return new TripDTO(trip.getId(), trip.getDestination().getId(), trip.getDepartureDate(), trip.getDepartureTime(), trip.getAvailableSeats());
    }

    public static Trip getTrip(TripDTO tripDTO) {
        Destination destination = new Destination(tripDTO.getDestinationId(), null);
        return new Trip(tripDTO.getId(), destination, tripDTO.getDepartureDate(), tripDTO.getDepartureTime(), tripDTO.getAvailableSeats());
    }
}
