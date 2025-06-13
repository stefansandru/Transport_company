using System.Data;
using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Configuration;
using NLog;


namespace persistance;

public class JdbcUtils
{
    private readonly string connectionString;
    // private static readonly ILogger logger;
    private static readonly Logger logger = LogManager.GetCurrentClassLogger(); // Use NLog's Logger


    private IDbConnection instance = null;

    public JdbcUtils()
    {
        // Load database configuration from appsettings.json
        var configuration = new ConfigurationBuilder()
            .AddJsonFile("appsettings.json")
            .Build();

        connectionString = configuration.GetConnectionString("DefaultConnection");
    }

    private IDbConnection GetNewConnection()
    {
        logger.Trace("Entering GetNewConnection");

        IDbConnection connection = null;
        try
        {
            connection = new SqliteConnection(connectionString);
            connection.Open();
            logger.Info("Successfully connected to the database.");
        }
        catch (SqliteException e)
        {
            logger.Error(e, "Error getting connection");
            Console.WriteLine($"Error getting connection: {e.Message}");
        }

        return connection;
    }

    public IDbConnection GetConnection()
    {
        logger.Trace("Entering GetConnection");
        try
        {
            if (instance == null || instance.State == ConnectionState.Closed)
            {
                instance = GetNewConnection();
            }
        }
        catch (Exception e)
        {
            logger.Error(e, "Error with database connection");
            Console.WriteLine($"Error DB: {e.Message}");
        }
        logger.Trace("Exiting GetConnection");
        return instance;
    }
}