using System.Collections.Generic;
using model;

namespace persistance;

public interface IReservedSeatRepository : IRepository<int, ReservedSeat>
{
    List<ReservedSeat> FindByTripDestinationDateTime(string destination, string dateString, string timeString);
}