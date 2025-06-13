using Avalonia.Controls;
using Avalonia.Interactivity;
using System;
using NLog;
using services;

namespace client;

public partial class LoginWindow : Window
{
    private static readonly Logger logger = LogManager.GetCurrentClassLogger();
    private readonly ITaskManagementServices server;

    public LoginWindow(ITaskManagementServices service)
    {
        InitializeComponent();
        this.server = service;
    }

    private void OnLoginButtonClick(object sender, RoutedEventArgs e)
    {
        var usernameTextBox = this.FindControl<TextBox>("Username");
        var passwordTextBox = this.FindControl<TextBox>("Password");
        
        string username = usernameTextBox?.Text?.Trim() ?? "";
        string password = passwordTextBox?.Text?.Trim() ?? "";
        
        if (string.IsNullOrEmpty(username) || string.IsNullOrEmpty(password))
        {
            ShowError("Please enter both username and password");
            return;
        }
        
        try
        {
            var mainWindow = new MainWindow(server);
            var employee = server.Login(username, password, mainWindow);
            mainWindow.setCurrentEmployee(employee);
            mainWindow.loadTrips();
            logger.Info($"User {username} logged in successfully");

            mainWindow.Show();
            this.Close();
        }
        catch (Exception ex)
        {
            logger.Error(ex, "Login error");
            ShowError($"Login failed: {ex.Message}");
        }
    }

    private void ShowError(string message)
    {
        var errorMessage = this.FindControl<TextBlock>("ErrorMessage");
        if (errorMessage != null)
        {
            errorMessage.Text = message;
            errorMessage.IsVisible = true;
        }
    }
}