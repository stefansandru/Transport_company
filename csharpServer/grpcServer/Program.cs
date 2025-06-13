using grpcServer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.DependencyInjection;
using persistance;

var builder = WebApplication.CreateBuilder(args);

builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenLocalhost(
        5000,
        o => o.Protocols = 
                Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http2
                );
});

builder.Services.AddGrpc();

builder.Services.AddSingleton<IClientRepository, ClientRepository>();
builder.Services.AddSingleton<IOfficeRepository, OfficeRepository>();
builder.Services.AddSingleton<IEmployeeRepository, EmployeeRepository>();
builder.Services.AddSingleton<IDestinationRepository, DestinationRepository>();
builder.Services.AddSingleton<ITripRepository, TripRepository>();
builder.Services.AddSingleton<IReservedSeatRepository, ReservedSeatRepository>();

var app = builder.Build();

app.MapGrpcService<TransportCompanyService>();

app.MapGet("/", () => "gRPC server running!");

Console.WriteLine(Path.GetFullPath("grpcServer/firmaTransport.db"));

app.Run();