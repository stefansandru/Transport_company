package ro.mpp2024.model;

public class Office {
    private Integer id;
    private String name;

    // Constructors
    public Office() {}

    public Office(Integer id, String name) {
        this.name = name;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}