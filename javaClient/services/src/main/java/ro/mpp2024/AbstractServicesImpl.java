package ro.mpp2024;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public abstract class AbstractServicesImpl implements IServices {
    @Override
    public Employee login(String username, String password, IObserver client) throws ServicesException {
        return null;
    }

    @Override
    public List<Trip> getAllTrips() throws ServicesException {
        return null;
    }
    @Override
    public List<SeatDTO> searchTripSeats(String destination, LocalDate date, LocalTime time) throws ServicesException {
        return null;
    }

    @Override
    public void reserveSeats(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) throws ServicesException {

    }

    @Override
    public Trip getTrip(String destination, LocalDate date, LocalTime time) throws ServicesException {
        return null;
    }
}
