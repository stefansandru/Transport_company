package ro.mpp2024.model;

import java.time.LocalDateTime;

public class ReservedSeat {
    private Integer id;
    private Trip trip;
    private Employee employee;
    private Integer seatNumber;
    private Client client;
    private LocalDateTime reservationDate = LocalDateTime.now();

    // Constructors
    public ReservedSeat() {}

    public ReservedSeat(
            Integer id,
            Trip trip,
            Employee employee,
            Integer seatNumber,
            Client client) {
        this.trip = trip;
        this.employee = employee;
        this.seatNumber = seatNumber;
        this.client = client;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
}