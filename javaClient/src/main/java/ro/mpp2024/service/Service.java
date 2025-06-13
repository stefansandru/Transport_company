package ro.mpp2024.service;

import ro.mpp2024.model.Client;
import ro.mpp2024.model.Employee;
import ro.mpp2024.model.ReservedSeat;
import ro.mpp2024.model.SeatDTO;
import ro.mpp2024.model.Trip;
import ro.mpp2024.repository.*;
import ro.mpp2024.utils.CryptPassword;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Service {
    private final IClientRepository clientRepository;
    private final IEmployeeRepository employeeRepository;
    private final IReservedSeatRepository reservedSeatRepository;
    private final ITripRepository tripRepository;

    public Service(IClientRepository clientRepository, IEmployeeRepository employeeRepository,
                   IReservedSeatRepository reservedSeatRepository, ITripRepository tripRepository) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.reservedSeatRepository = reservedSeatRepository;
        this.tripRepository = tripRepository;
    }

    public Employee login(String username, String password) {
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee == null) {
            System.out.println("null");
            throw new IllegalArgumentException("invalid name");
        } else {
            return employeeRepository.findByUsername(username)
                    .filter(emp -> CryptPassword.checkPassword(password, emp.getPassword()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid password"));
        }
    }

    public Iterable<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<SeatDTO> searchTripSeats(String destination, LocalDate date, LocalTime time) {
        String dateString = date.toString();
        String timeString = time.toString();

        List<ReservedSeat> reservedSeats = reservedSeatRepository.
                findByTripDestinationDateTime(destination, dateString, timeString);

        if (reservedSeats.isEmpty()) {
            System.out.println("Trip not found");
        }
        List<SeatDTO> allSeats = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            final int seatNumber = i;
            String clientName = reservedSeats.stream()
                    .filter(seat -> seat.getSeatNumber() == seatNumber)
                    .map(seat -> seat.getClient().getName())
                    .findFirst()
                    .orElse("-");
            
            allSeats.add(new SeatDTO(i, clientName));
        }
    
        return allSeats;
    }

    public void reserveSeats(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) {
        // Save the client if it doesn't exist
        Client client = clientRepository.findByUsername(clientName).orElseGet(() -> {
            Client addClient = new Client(null, clientName);
            return clientRepository.save(addClient).orElse(null);
        });

        // save reserved seats
        for (Integer seatNumber : seatNumbers) {
            ReservedSeat reservedSeat = new ReservedSeat(
                    null, trip, employee, seatNumber, client);
            reservedSeatRepository.save(reservedSeat);
        }

        // Update the number of available seats for the trip
        trip.setAvailableSeats(trip.getAvailableSeats() - seatNumbers.size());
        tripRepository.update(trip);
    }

    public Trip getTrip(String destination, LocalDate date, LocalTime time) {
        String dateString = date.toString();
        String timeString = time.toString();
        Trip trip = tripRepository.findByDestinationAndDateAndTime(destination, dateString, timeString).orElse(null);
        if (trip == null) {
            System.out.println("Trip not found");
        }
        System.out.println("service : trip.getId()");
        System.out.println(trip.getId());
        return trip;
    }
}