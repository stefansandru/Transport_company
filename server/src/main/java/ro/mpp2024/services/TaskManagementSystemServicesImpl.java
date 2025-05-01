package ro.mpp2024.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ro.mpp2024.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManagementSystemServicesImpl extends AbstractServicesImpl {
    private final IClientRepository clientRepository;
    private final IEmployeeRepository employeeRepository;
    private final IReservedSeatRepository reservedSeatRepository;
    private final ITripRepository tripRepository;

    private static final Logger logger = LogManager.getLogger(TaskManagementSystemServicesImpl.class);

    private Map<Integer, IObserver> loggedEmployees;

    private final int defaultThreadsNo = 5;

    public TaskManagementSystemServicesImpl(IClientRepository clientRepository,
                                        IEmployeeRepository employeeRepository,
                                        IReservedSeatRepository reservedSeatRepository,
                                        ITripRepository tripRepository) {
        logger.trace("Entering TaskManagementSystemServicesImpl constructor");
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.reservedSeatRepository = reservedSeatRepository;
        this.tripRepository = tripRepository;
        loggedEmployees = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Employee login(String username, String password, IObserver client) throws ServicesException {
        System.out.println(CryptPassword.hashPassword("aba"));
        logger.trace("Entering login");
        Employee employee = employeeRepository.findByUsername(username).orElse(null);
        if (employee == null) {
            logger.info("Employee with username {} not found", username);
            throw new ServicesException("invalid name");
        } else {
            if (loggedEmployees.containsKey(employee.getId())) {
                throw new ServicesException("Admin already logged.");
            }
            if (!CryptPassword.checkPassword(password, employee.getPassword())) {
                throw new ServicesException("Invalid password");
            }
            loggedEmployees.put(employee.getId(), client);
            return employee;
        }
    }

    @Override
    public synchronized void logout(Employee employee) throws ServicesException {
        IObserver localClient=loggedEmployees.remove(employee.getId());
        if (localClient==null)
            throw new ServicesException(employee+" is not logged in.");
    }

    @Override
    public synchronized List<Trip> getAllTrips() {
        logger.trace("Entering getAllTrips");
        List<Trip> trips = new ArrayList<>();
        tripRepository.findAll().forEach(trips::add);
        return trips;
    }

    @Override
    public synchronized List<SeatDTO> searchTripSeats(String destination, LocalDate date, LocalTime time) {
        logger.trace("Entering searchTripSeats: {} {} {}", destination, date, time);
        String dateString = date.toString();
        String timeString = time.toString();

        List<ReservedSeat> reservedSeats = reservedSeatRepository.
                findByTripDestinationDateTime(destination, dateString, timeString);

        if (reservedSeats.isEmpty()) {
            logger.info("No reserved seats found for trip with destination {}, date {}, time {}", destination, dateString, timeString);
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

    @Override
    public synchronized void reserveSeats(String clientName, List<Integer> seatNumbers, Trip trip, Employee employee) {
        logger.trace("Entering reserveSeats");
        logger.info("Reserving seats for client {}: {}", clientName, seatNumbers);
        logger.info("Trip: {}", trip);
        logger.info("Employee: {}", employee);

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

        // Notify the logged employees about the reservation
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (Map.Entry<Integer, IObserver> entry : loggedEmployees.entrySet()) {
            IObserver observer = entry.getValue();
            if (!Objects.equals(entry.getKey(), employee.getId())) {
                executor.execute(() -> {
                    try {
                        logger.debug("Notifying employee ID {} {} (seats reserved)", entry.getKey(), observer);
                        observer.seatsReserved();
                    } catch (ServicesException e) {
                        logger.error("Error notifying client about reservation: {}", e.getMessage());
                    }
                });
            }
        }
        executor.shutdown();
    }

    @Override
    public synchronized Trip getTrip(String destination, LocalDate date, LocalTime time) {
        logger.trace("Entering getTrip");
        String dateString = date.toString();
        String timeString = time.toString();
        Trip trip = tripRepository.findByDestinationAndDateAndTime(destination, dateString, timeString).orElse(null);
        if (trip == null) {
            System.err.println("Trip not found");
        }
        return trip;
    }
}