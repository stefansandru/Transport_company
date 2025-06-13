using Avalonia;
using Avalonia.Markup.Xaml;
using System;
using System.Configuration;
using Avalonia.Controls.ApplicationLifetimes;
using networking.jsonprotocol;

namespace client
{
    public class App : Application
    {
        private static int defaultChatPort = 55556;
        private static string defaultServer = "localhost";

        public override void Initialize()
        {
            AvaloniaXamlLoader.Load(this);
        }

        public override void OnFrameworkInitializationCompleted()
        {

            // read from App.config
            string serverIP = ConfigurationManager.AppSettings["server.host"] ?? defaultServer;
            int serverPort = defaultChatPort;

            string portValue = ConfigurationManager.AppSettings["server.port"];
            if (portValue != null && int.TryParse(portValue, out int parsedPort))
            {
                serverPort = parsedPort;
            }

            Console.WriteLine($"Using server IP: {serverIP}, port: {serverPort}");

            // create proxy RPC
            var server = new TaskManagementServicesJsonProxy(serverIP, serverPort);

            var loginWindow = new LoginWindow(server);

            if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
            {
                loginWindow.Show();
                // mai adauga un window
            }
            
            base.OnFrameworkInitializationCompleted();
        }

        [STAThread]
        public static void Main(string[] args)
        {
            BuildAvaloniaApp().StartWithClassicDesktopLifetime(args);
        }

        public static AppBuilder BuildAvaloniaApp()
            => AppBuilder.Configure<App>()
                .UsePlatformDetect();
    }
}