package ro.mpp2024.model;

public class Destination extends Entity<Integer> {
    private String name;

    // Constructors
    public Destination() {
        super();
    }

    public Destination(Integer id, String name) {
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