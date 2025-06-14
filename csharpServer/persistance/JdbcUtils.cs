using System.Data;
using Microsoft.Data.Sqlite;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;


namespace persistance;

public class JdbcUtils
{
    private readonly string connectionString;
    private readonly ILogger<JdbcUtils> _logger;
    private IDbConnection instance = null;

    public JdbcUtils(ILogger<JdbcUtils> logger)
    {
        _logger = logger;

        // Load database configuration from appsettings.json
        var configuration = new ConfigurationBuilder()
            .SetBasePath(AppContext.BaseDirectory)
            .AddJsonFile("appsettings.json")
            .Build();

        var rawConnectionString = configuration.GetConnectionString("DefaultConnection");
        var dataSourcePrefix = "Data Source=";
        if (rawConnectionString.StartsWith(dataSourcePrefix))
        {
            var dbPath = rawConnectionString.Substring(dataSourcePrefix.Length);
            if (!Path.IsPathRooted(dbPath))
            {
                dbPath = Path.Combine(AppContext.BaseDirectory, dbPath);
                rawConnectionString = $"{dataSourcePrefix}{dbPath}";
            }
        }
        _logger.LogInformation($"Using connection string: {rawConnectionString}");
        connectionString = rawConnectionString;

        _logger.LogDebug($"Checking DB file: {rawConnectionString}");
        var dbPathToCheck = rawConnectionString.StartsWith(dataSourcePrefix)
            ? rawConnectionString.Substring(dataSourcePrefix.Length)
            : rawConnectionString;
        if (!File.Exists(dbPathToCheck))
        {
            _logger.LogError($"Database file does not exist at: {dbPathToCheck}");
        }
        else
        {
            _logger.LogDebug($"Database file found at: {dbPathToCheck}");
        }
    }

    private IDbConnection GetNewConnection()
    {
        IDbConnection connection = null;
        try
        {
            connection = new SqliteConnection(connectionString);
            connection.Open();
            _logger.LogInformation("Successfully connected to the database.");
        }
        catch (SqliteException e)
        {
            _logger.LogError(e, "Error getting connection");
            Console.WriteLine($"Error getting connection: {e.Message}");
        }

        return connection;
    }

    public IDbConnection GetConnection()
    {
        try
        {
            if (instance == null || instance.State == ConnectionState.Closed)
            {
                instance = GetNewConnection();
            }
        }
        catch (Exception e)
        {
            _logger.LogError(e, "Error with database connection");
            Console.WriteLine($"Error DB: {e.Message}");
        }
        return instance;
    }
}