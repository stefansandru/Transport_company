package ro.mpp2024.jsonProtocol;

import ro.mpp2024.Employee;
import ro.mpp2024.SeatDTO;
import ro.mpp2024.Trip;
import ro.mpp2024.dto.DTOUtils;
import ro.mpp2024.dto.EmployeeDTO;
import ro.mpp2024.dto.TripDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Request {
    private RequestType requestType;
    private String username;
    private String password;
    private EmployeeDTO employeeDTO;
    private TripDTO tripDTO;
    private DTOUtils dtoUtils;
    private List<Trip> trips;
    private List<SeatDTO> seats;
    private Employee curentEmployee;
    private Trip tripToReserve;
    private String clientName;
    private List<Integer> seatsNumbers;
    private String tripDestination;
    private LocalDate tripDate;
    private LocalTime tripTime;
    private String destination;

    public Request() {
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeDTO getEmployeeDTO() {
        return employeeDTO;
    }

    public void setEmployeeDTO(EmployeeDTO employeeDTO) {
        this.employeeDTO = employeeDTO;
    }

    public TripDTO getTripDTO() {
        return tripDTO;
    }

    public void setTripDTO(TripDTO tripDTO) {
        this.tripDTO = tripDTO;
    }

    public DTOUtils getDtoUtils() {
        return dtoUtils;
    }

    public void setDtoUtils(DTOUtils dtoUtils) {
        this.dtoUtils = dtoUtils;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<SeatDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatDTO> seats) {
        this.seats = seats;
    }

    public Employee getCurentEmployee() {
        return curentEmployee;
    }

    public void setCurentEmployee(Employee curentEmployee) {
        this.curentEmployee = curentEmployee;
    }

    public Trip getTripToReserve() {
        return tripToReserve;
    }

    public void setTripToReserve(Trip tripToReserve) {
        this.tripToReserve = tripToReserve;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<Integer> getSeatsNumbers() {
        return seatsNumbers;
    }

    public void setSeatsNumbers(List<Integer> seatsNumbers) {
        this.seatsNumbers = seatsNumbers;
    }

    public String getTripDestination() {
        return tripDestination;
    }

    public void setTripDestination(String tripDestination) {
        this.tripDestination = tripDestination;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public LocalTime getTripTime() {
        return tripTime;
    }

    public void setTripTime(LocalTime tripTime) {
        this.tripTime = tripTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", employeeDTO=" + employeeDTO +
                ", tripDTO=" + tripDTO +
                '}';
    }
}