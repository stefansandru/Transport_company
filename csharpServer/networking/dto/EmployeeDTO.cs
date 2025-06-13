namespace networking.dto;

public class EmployeeDTO
{
    public int? Id { get; set; }
    public string Username { get; }
    public string Password { get; }
    public int? OfficeId { get; }

    public EmployeeDTO(int? id, string username, string password, int? officeId)
    {
        Id = id;
        Username = username;
        Password = password;
        OfficeId = officeId;
    }

    public override string ToString()
    {
        return $"EmployeeDTO{{id={Id}, username='{Username}', password='{Password}', officeId={OfficeId}}}";
    }
}