package ro.mpp2024.repository;

import ro.mpp2024.model.Employee;
import ro.mpp2024.model.ReservedSeat;
import ro.mpp2024.model.Trip;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface IReservedSeatRepository extends IRepository<Integer, ReservedSeat> {

    List<ReservedSeat> findByTripDestinationDateTime(String destination, String date, String time);

//    boolean reserveSeats(String clientName, int numberOfSeats, Trip trip, Employee employee);
}
