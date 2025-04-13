package ro.mpp2024.model;

import java.time.LocalDateTime;

public class ReservedSeat extends Entity<Integer> {
    private Trip trip;
    private Employee employee;
    private Integer seatNumber;
    private Client client;

    // Constructors
    public ReservedSeat() {
        super();
    }

    public ReservedSeat(
            Integer id,
            Trip trip,
            Employee employee,
            Integer seatNumber,
            Client client) {
        super(id);
        this.trip = trip;
        this.employee = employee;
        this.seatNumber = seatNumber;
        this.client = client;
    }

    // Getters and setters
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
}