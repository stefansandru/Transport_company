package ro.mpp2024;

public interface IObserver {
    // va notifica clientul ca un angajat s-a logat
    void employeeLoggedIn(Employee employee) throws ServicesException;

    // va notifica clientul ca un loc a fost rezervat
    void seatsReserved() throws ServicesException;
}
