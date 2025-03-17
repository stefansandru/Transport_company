package ro.mpp2024.model;

import ro.mpp2024.repository.ReservedSeatRepository;

public class Client extends Entity<Integer> {
    private String name;

    // Constructors
    public Client() {
        super();
    }

    public Client(Integer id, String name) {
        super(id);
        this.name = name;
    }
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}