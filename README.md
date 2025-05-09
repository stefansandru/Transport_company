# Transport Company

*Java Project for managing transport company data (employees, clients, rides, and more) using JDBC and a structured repository pattern.*

## Features

* CRUD operations for various entities (employees, clients, rides, etc.)
* Modular repository pattern with abstract and concrete implementations
* Uses JDBC for database operations
* Logging integrated using Log4j2
* Follows clean code and best practices

## Requirements

* Java 17
* Maven or Gradle for dependency management
* PostgreSQL/MySQL database (or update `JdbcUtils` for your DB)
* Log4j2

## Getting Started

1. Clone the repo:
   ```
   git clone https://github.com/stefansandru/Transport_company.git
   ```
2. Configure your `application.properties` for database access.
3. Build and run the project with your preferred Java IDE or using Maven/Gradle.

## Structure

* `ro.mpp2024`: Main package containing repositories and entity models
* `ro.mpp2024.utils.JdbcUtils`: Helper class for JDBC connection management

## License

*MIT License*  
