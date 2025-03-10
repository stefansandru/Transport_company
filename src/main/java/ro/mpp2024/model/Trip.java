package ro.mpp2024.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Trip {
    private Integer id;
    private Destination destination;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private Integer availableSeats;

    // Constructors
    public Trip() {}

    public Trip(
            Integer id,
            Destination destination,
            LocalDate departureDate,
            LocalTime departureTime,
            Integer availableSeats) {
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
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
}