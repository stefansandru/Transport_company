using System;
using System.ComponentModel;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace ro.mpp2024.testrest
{
    public class TestRestClient
    {
        private static readonly string BASE_URL = "http://localhost:8080/transport/api/trips";
        private static readonly HttpClient httpClient = new HttpClient();

        public static async Task Main(string[] args)
        {
            Console.WriteLine("Starting TestRestClient...");

            try
            {
                Trip newTrip = CreateTrip();
                Console.WriteLine("Created trip object: " + newTrip);

                var json = JsonSerializer.Serialize(newTrip);
                Console.WriteLine("JSON sent: " + json);
                
                Trip addedTrip = await AddTrip(newTrip);
                Console.WriteLine("Added trip: " + addedTrip);

                Console.WriteLine("All trips:");
                await PrintAllTrips();

                int? tripId = addedTrip?.Id;
                Console.WriteLine("Trip ID: " + tripId);

                if (tripId.HasValue)
                {
                    await DeleteTrip(tripId.Value);
                    Console.WriteLine("Deleted trip with ID: " + tripId);

                    await VerifyDeletion(tripId.Value);
                }

                Console.WriteLine("TestRestClient completed successfully!");
            }
            catch (Exception e)
            {
                Console.Error.WriteLine("Error in TestRestClient: " + e.Message);
                Console.Error.WriteLine(e);
            }
        }

        private static async Task PrintAllTrips()
        {
            var trips = await httpClient.GetFromJsonAsync<Trip[]>(BASE_URL);
            if (trips != null)
            {
                foreach (var t in trips)
                {
                    Console.WriteLine("Trip: " + t);
                }
            }
        }

        private static Trip CreateTrip()
        {
            var destination = new Destination { Id = 1, Name = "Test Destination" };
            return new Trip
            {
                Id = null,
                Destination = destination,
                Date = DateTime.Now.AddDays(1).Date,
                Time = new TimeSpan(10, 0, 0),
                Seats = 50
            };
        }

        private static async Task<Trip> AddTrip(Trip trip)
        {
            var response = await httpClient.PostAsJsonAsync(BASE_URL, trip);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadFromJsonAsync<Trip>();
        }

        private static async Task DeleteTrip(int tripId)
        {
            var response = await httpClient.DeleteAsync($"{BASE_URL}/{tripId}");
            response.EnsureSuccessStatusCode();
        }

        private static async Task VerifyDeletion(int tripId)
        {
            var response = await httpClient.GetAsync($"{BASE_URL}/{tripId}");
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                Console.WriteLine($"Verification successful: Trip with ID {tripId} was deleted");
            }
            else
            {
                Console.Error.WriteLine($"Trip was not deleted! It still exists with ID: {tripId}");
            }
        }
    }

    public class Trip
    {
        [JsonPropertyName("id")]
        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public int? Id { get; set; }

        [JsonPropertyName("destination")]
        public Destination Destination { get; set; }

        [JsonPropertyName("departureDate")]
        [JsonConverter(typeof(DateOnlyConverter))]
        public DateTime Date { get; set; }

        [JsonPropertyName("departureTime")]
        public TimeSpan Time { get; set; }

        [JsonPropertyName("availableSeats")]
        public int Seats { get; set; }

        public override string ToString() =>
            $"Trip(Id={Id}, Destination={Destination}, Date={Date:yyyy-MM-dd}, Time={Time}, Seats={Seats})";
    }
    
    public class DateOnlyConverter : JsonConverter<DateTime>
    {
        public override DateTime Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
            => DateTime.ParseExact(reader.GetString(), "yyyy-MM-dd", null);

        public override void Write(Utf8JsonWriter writer, DateTime value, JsonSerializerOptions options)
            => writer.WriteStringValue(value.ToString("yyyy-MM-dd"));
    }

    public class Destination
    {
        public int Id { get; set; }
        public string Name { get; set; }

        public override string ToString() => $"Destination(Id={Id}, Name={Name})";
    }
}