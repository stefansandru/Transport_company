package ro.mpp2024.utils;

import ro.mpp2024.Destination;
import ro.mpp2024.Trip;
import ro.mpp2024.proto.TripDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class DTOUtils {

    // Conversie din TripDTO (clasa generată de Protobuf) în Trip (model aplicație)
    public static Trip convertTripDTOToTrip(TripDTO tripDTO) {
        // Presupunem că destination este String și corespunde cu un destinatar din aplicația ta, eventual cu un id sau nume
        Destination destination = new Destination(null, tripDTO.getDestination()); // sau adaptează după modelul tău de Destination
        LocalDate date = LocalDate.parse(tripDTO.getDate());
        LocalTime time = LocalTime.parse(tripDTO.getTime());
        return new Trip(
                tripDTO.getId(),
                destination,
                date,
                time,
                tripDTO.getAvailableSeats()
        );
    }

    // Poți adăuga și conversia inversă, dacă e nevoie:
    public static TripDTO convertTripToTripDTO(Trip trip) {
        return TripDTO.newBuilder()
                .setId(trip.getId())
                .setDestination(trip.getDestination().getName()) // adaptează dacă ai id sau altceva
                .setDate(trip.getDepartureDate().toString())
                .setTime(trip.getDepartureTime().toString())
                .setAvailableSeats(trip.getAvailableSeats())
                .build();
    }

    // ... restul utilitarelor pentru Employee, Seat, etc.
}