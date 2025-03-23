package ro.mpp2024.model;

public class Office extends Entity<Integer> {
    private String name;

    // Constructors
    public Office() {
        super();
    }

    public Office(Integer id, String name) {
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

    @Override
    public String toString() {
        return "Id=" + getId() + " " + name;
    }
}