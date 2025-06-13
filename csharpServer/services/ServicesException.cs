namespace services;

public class ServicesException : Exception
{
    public ServicesException() : base() { }

    public ServicesException(string msg) : base(msg) { }

    public ServicesException(string msg, Exception ex) : base(msg, ex) { }
}
