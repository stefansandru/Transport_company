package ro.mpp2024.model;

public class Employee {
    private Integer id;
    private String username;
    private String password;
    private Office office;

    // Constructors
    public Employee() {}

    public Employee(Integer id, String username, String password, Office office) {
        this.username = username;
        this.password = password;
        this.office = office;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }
}