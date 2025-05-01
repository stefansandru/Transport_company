package ro.mpp2024.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TripDTO {
    private Integer id;
    private Integer destinationId;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private Integer availableSeats;

    public TripDTO(Integer id, Integer destinationId, LocalDate departureDate, LocalTime departureTime, Integer availableSeats) {
        this.id = id;
        this.destinationId = destinationId;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    @Override
    public String toString() {
        return "TripDTO{" +
                "id=" + id +
                ", destinationId=" + destinationId +
                ", departureDate=" + departureDate +
                ", departureTime=" + departureTime +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
