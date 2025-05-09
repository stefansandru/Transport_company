# Transport Company

*Java application for managing the operations of a transport company using a client-server architecture, with real-time updates and robust logging.*

## Features

* Client-server implementation for distributed access and real-time communication.
* Observable UI updates to reflect server-side changes.
* Centralized logging using Log4j2 for traceability and debugging.
* CRUD operations on key entities (employees, clients, rides, etc.)
* Modular codebase with repository pattern and layered architecture.
* Integration with relational database through JDBC.

## Client-Server Implementation

* The project uses a client-server architecture:  
  * **Server:** Handles authentication, database access (using JDBC), business logic, and broadcasting updates to clients.
  * **Clients:** Connect via sockets, send requests (e.g., login, reserve seats, search trips), and receive responses or real-time updates.
* Communication protocol is JSON-based, utilizing custom request and response objects.
* All client-side UI components observe model changes and update automatically when notified by the server, ensuring that reservations, changes, and searches are always up-to-date across all users.

## Logging

* Integrated Log4j2 framework for detailed tracing of requests, responses, and system actions, aiding in problem diagnosis and audit trails.

## Requirements

* Java 17
* Maven or Gradle for dependency management
* PostgreSQL/MySQL database (or update `JdbcUtils` for your DB)
* Log4j2
