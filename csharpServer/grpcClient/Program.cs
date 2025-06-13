using Grpc.Net.Client;
using System.Net.Http;
using TransportCompany.GrpcServer;

// var channel = GrpcChannel.ForAddress("http://localhost:5000");

var httpHandler = new HttpClientHandler();
httpHandler.ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator;

var channel = GrpcChannel.ForAddress("http://localhost:5000");


var client = new TransportCompany.GrpcServer.TransportCompany.TransportCompanyClient(channel);

Console.WriteLine("Introdu username:");
var username = Console.ReadLine();

Console.WriteLine("Introdu password:");
var password = Console.ReadLine();

var request = new LoginRequest { Username = username, Password = password };
try
{
    var reply = await client.LoginAsync(request);

    if (reply.EmployeeId > 0)
        Console.WriteLine($"Login reușit! Id: {reply.EmployeeId}, Username: {reply.Username}");
    else
        Console.WriteLine("Login eșuat! Username sau parolă greșită.");
}
catch (Exception ex)
{
    Console.WriteLine($"Eroare la apel: {ex}");
}