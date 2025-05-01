package ro.mpp2024;

import java.util.List;


public interface IReservedSeatRepository extends IRepository<Integer, ReservedSeat> {

    List<ReservedSeat> findByTripDestinationDateTime(String destination, String date, String time);

//    boolean reserveSeats(String clientName, int numberOfSeats, Trip trip, Employee employee);
}
