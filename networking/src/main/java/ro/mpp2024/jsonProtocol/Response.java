package ro.mpp2024.jsonProtocol;

import ro.mpp2024.Employee;
import ro.mpp2024.SeatDTO;
import ro.mpp2024.Trip;
import ro.mpp2024.dto.DTOUtils;
import ro.mpp2024.dto.EmployeeDTO;
import ro.mpp2024.dto.TripDTO;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private ResponseType responseType;
    private String errorMessage;
    private EmployeeDTO loggedInEmployeeDTO;
    private TripDTO tripDTO;
    private DTOUtils dtoUtils;
    private List<Trip> trips;
    private List<SeatDTO> seats;
    private Employee loggedEmployee;
    private Trip trip;

    public Response() {
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public EmployeeDTO getLoggedInEmployeeDTO() {
        return loggedInEmployeeDTO;
    }

    public void setLoggedInEmployeeDTO(EmployeeDTO loggedInEmployeeDTO) {
        this.loggedInEmployeeDTO = loggedInEmployeeDTO;
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

    public Employee getLoggedEmployee() {
        return loggedEmployee;
    }

    public void setLoggedEmployee(Employee loggedEmployee) {
        this.loggedEmployee = loggedEmployee;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip reservedTrip) {
        this.trip = reservedTrip;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestType=" + responseType +
                ", errorMessage='" + errorMessage + '\'' +
                ", loggedEmployee=" + loggedEmployee +
                ", reservedTrip=" + trip +
                '}';
    }
}