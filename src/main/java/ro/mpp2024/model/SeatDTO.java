package ro.mpp2024.model;

public class SeatDTO {
    private final int seatNumber;
    private final String clientName;

    public SeatDTO(int seatNumber, String clientName) {
        this.seatNumber = seatNumber;
        this.clientName = clientName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getClientName() {
        return clientName == null ? "-" : clientName;
    }
}