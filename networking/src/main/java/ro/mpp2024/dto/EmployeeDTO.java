package ro.mpp2024.dto;

public class EmployeeDTO {
    private Integer id;
    private final String username;
    private final String password;
    private final Integer officeId;

    public EmployeeDTO(Integer id, String username, String password, Integer officeId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.officeId = officeId;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", officeId=" + officeId +
                '}';
    }
}
