# Transport Company Management System

A tiny full-stack demo app built for the 2024 MPP coursework.  
It helps a (very) small passenger-transport company keep track of trips, seats and employees.

---

## 1.  What lives where?

```
transport-company/
 ├─ csharpServer/         # ASP-NET Core gRPC backend
 │   ├─ grpcServer/       # gRPC service implementation
 │   ├─ model/            # Domain entities (shared with client through .proto files)
 │   └─ persistance/      # Pure ADO.NET repositories & DB helpers
 │
 └─ javaClient/           # Thin Java-FX GUI client – talks to the server over gRPC
     ├─ fxClient/         # UI, controllers and DTO helpers
     ├─ services/         # Service / Observer interfaces
     └─ model/            # Same domain entities, but written in Java
```

*Why so many sub-projects?*  
Keeping the backend and frontend in their own Gradle / dotnet projects makes the build quicker and avoids the usual CLASSPATH / DLL-hell.

---

## 2.  Prerequisites

1. .NET 8 SDK (tested on 8.0.102)
2. Java 17 (or newer) + Gradle 8
3. PostgreSQL 15 (or change the connection string in `csharpServer/persistance/appsettings.json`)
4. Optional – VS Code / Rider / IntelliJ IDEA for a nicer dev experience

---

## 3.  Running the app

### Backend – C#

```bash
cd csharpServer/grpcServer
# restore & run
 dotnet run
```

The server listens on `https://localhost:5001` (see `Program.cs`).  
Feel free to tweak ports in `appsettings.json`.

### Frontend – JavaFX

```bash
cd javaClient/fxClient
./gradlew run
```

At login use one of the test accounts inserted by `DatabaseSeeder` (username `ana`, password `secret`).

---

## 4.  Database quick-start

```bash
psql -U postgres
CREATE DATABASE transport_company;
\c transport_company
\i sql/schema.sql          -- tables
\i sql/seed-data.sql       -- demo data (employees, trips, etc.)
```

> If Postgres is not your thing you can switch to SQLite by swapping the connection string and *most* things will still work.

---

## Test Users

| Username | Password |
|----------|----------|
| ana      | ana      |
| adi      | adi      |

> The password for each account is the same as the username.

*Use these accounts to log in and test the application.*

