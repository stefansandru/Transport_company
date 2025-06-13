using System;
using System.Collections.Generic;
using System.Linq;
using Avalonia.Controls;
using Avalonia.Interactivity;
using model;
using services;
using Avalonia.Threading;

namespace client;

public partial class MainWindow : Window, IObserver
{
    private static readonly NLog.Logger logger = NLog.LogManager.GetCurrentClassLogger();
    private readonly ITaskManagementServices server;
    private Employee currentEmployee;
    private Trip tripToReserve;
    private List<Trip> trips;

    private ListBox tripsListBox; 
    private TextBox searchDestinationField;
    private ListBox seatsListBox;
    private TextBox clientNameField;
    private TextBox seatNumbersField;
    private Button reserveButton;
    private Button logoutButton;


    
    public MainWindow(ITaskManagementServices server)
    {
        this.server = server ?? throw new ArgumentNullException(nameof(server));
        InitializeComponent();
        tripsListBox = this.FindControl<ListBox>("TripsListBox");
        clientNameField = this.FindControl<TextBox>("ClientNameField");
        seatNumbersField = this.FindControl<TextBox>("SeatNumbersField");
        reserveButton = this.FindControl<Button>("ReserveButton");
        logoutButton = this.FindControl<Button>("LogoutButton");

        
        TripsListBox.SelectionChanged += TripsListBox_SelectionChanged;
        reserveButton.Click += ReserveButton_Click;
        logoutButton.Click += LogoutButton_Click;

    }
    
    public void setCurrentEmployee(Employee employee)
    {
        currentEmployee = employee;
    }
    
    public void loadTrips()
    {
        try
        {
            // 1. Salvezi itemul selectat (Trip) ca referință (sau, mai sigur, după Id dacă Tripuri diferă ca instance)
            Trip selectedTrip = TripsListBox.SelectedItem as Trip;
            int? selectedTripId = (selectedTrip != null) ? selectedTrip.Id : (int?)null;

            trips = server.GetAllTrips();
            TripsListBox.ItemsSource = trips;

            // 2. Re-seleci itemul după Id (dacă încă există)
            if (selectedTripId.HasValue)
            {
                var tripToReselect = trips.FirstOrDefault(t => t.Id == selectedTripId.Value);
                if (tripToReselect != null)
                    TripsListBox.SelectedItem = tripToReselect;
            }
        }
        catch (Exception ex)
        {
            logger.Error(ex, "Error loading trips");
        }
    }


    private void TripsListBox_SelectionChanged(object? sender, SelectionChangedEventArgs e)
    {
        tripToReserve = TripsListBox.SelectedItem as Trip;
        
        if (tripToReserve != null)
        {
            var seats = server.SearchTripSeats(
                tripToReserve.Destination.ToString(),
                tripToReserve.DepartureDate,
                tripToReserve.DepartureTime
            );

            // Afișează locurile libere în SeatsListBox
            SeatsListBox.ItemsSource = seats.Select(s => s.ToString()).ToList();
        }
        else
        {
            // goliți seats dacă nu e selectat nimic
            SeatsListBox.ItemsSource = null;
        }
    }
    
    private async void ReserveButton_Click(object? sender, RoutedEventArgs e)
    {
        try
        {
            if (tripToReserve == null)
            {
                await MessageBox("Please select a trip first!");
                return;
            }

            if (currentEmployee == null)
            {
                await MessageBox("No employee is logged in!");
                return;
            }

            string clientName = clientNameField.Text?.Trim();
            string seatNumbersText = seatNumbersField.Text?.Trim();

            if (string.IsNullOrWhiteSpace(clientName))
            {
                await MessageBox("Please enter the client's name!");
                return;
            }

            if (string.IsNullOrWhiteSpace(seatNumbersText))
            {
                await MessageBox("Please enter seat numbers separated by comma.");
                return;
            }

            // Parse seat numbers from CSV input
            var seatNumbers = seatNumbersText
                                .Split(new[] {',', ';', ' '}, StringSplitOptions.RemoveEmptyEntries)
                                .Select(s => int.TryParse(s.Trim(), out int seat) ? seat : -1)
                                .Where(seat => seat > 0)
                                .ToList();

            if (seatNumbers.Count == 0)
            {
                await MessageBox("Invalid seat numbers!");
                return;
            }

            server.ReserveSeats(clientName, seatNumbers, tripToReserve, currentEmployee);
            await MessageBox("Reservation successful!");
            SeatsReserved();
            seatNumbersField.Text = "";

        }
        catch (Exception ex)
        {
            logger.Error(ex, "Error on reserve");
            await MessageBox("Reservation failed: " + ex.Message);
        }
    }

    public void SeatsReserved()
    {
        Dispatcher.UIThread.Post(() =>
        {
            loadTrips();
            if (tripToReserve != null)
            {
                var seats = server.SearchTripSeats(
                    tripToReserve.Destination.ToString(),
                    tripToReserve.DepartureDate,
                    tripToReserve.DepartureTime
                );
                SeatsListBox.ItemsSource = seats.Select(s => s.ToString()).ToList();
            }
            else
            {
                SeatsListBox.ItemsSource = null;
            }
        });
    }
    
    // Simplu MessageBox helper (poți folosi și un dialog custom)
    private async System.Threading.Tasks.Task MessageBox(string message)
    {
        await new Window
        {
            Title = "Info",
            Content = new TextBlock { Text = message, Margin = new Avalonia.Thickness(20) },
            Width = 300,
            Height = 120,
            WindowStartupLocation = WindowStartupLocation.CenterOwner,
            ShowInTaskbar = false
        }.ShowDialog(this);
    }

    private async void LogoutButton_Click(object? sender, RoutedEventArgs e)
    {
        try
        {
            if (currentEmployee == null)
            {
                await MessageBox("No employee is logged in!");
                return;
            }

            server.Logout(currentEmployee);

            await MessageBox("You have been logged out!");

            // Închide fereastra principală (teoretic poți redeschide fereastra de login, dacă există)
            this.Close();
            // Dacă ai un login window, aici îl poți deschide.
        }
        catch (Exception ex)
        {
            logger.Error(ex, "Logout failed");
            await MessageBox("Logout failed: " + ex.Message);
        }
    }

}