# LankaTex Database Setup

Database Management System: Microsoft SQL Server  
Database Tool: SQL Server Management Studio (SSMS)

## Database Name

LankaTexDB

## Developer Setup

1. Install or open Microsoft SQL Server and SQL Server Management Studio (SSMS).

2. Create a database named:

   LankaTexDB

3. Enable TCP/IP for SQL Server.

4. Use TCP port:

   1433

5. The Spring Boot application uses Microsoft SQL Server.

6. Default local database connection:

   jdbc:sqlserver://localhost:1433;databaseName=LankaTexDB;encrypt=true;trustServerCertificate=true;integratedSecurity=true

7. Run the Spring Boot application after creating the database.

## Development Environment

- Java: 21
- Spring Boot: 4.1.1
- Database: Microsoft SQL Server
- IDE: IntelliJ IDEA
- Database Management Tool: SSMS