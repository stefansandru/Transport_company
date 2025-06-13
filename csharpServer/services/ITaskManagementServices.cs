using model;

namespace services;

public interface ITaskManagementServices
{
    Employee Login(string username, string password, IObserver client);
    void Logout(Employee employee);
    List<Trip> GetAllTrips();
    List<SeatDTO> SearchTripSeats(string destination, DateOnly date, TimeOnly time);
    void ReserveSeats(string clientName, List<int> seatNumbers, Trip trip, Employee employee);
    Trip GetTrip(string destination, DateOnly date, TimeOnly time);
}
