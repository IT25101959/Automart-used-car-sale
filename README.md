# AutoMart - Second-Hand Car Selling Application

This is a complete, modern Spring Boot web application built using the MVC architecture.

## Features Implemented
- **OOP Principles:** Full use of Encapsulation, Abstraction, Inheritance, and Polymorphism in entity models.
- **CRUD Operations:** Complete end-to-end functionality for Vehicles, Users, Spare Parts, and Sell Requests.
- **Modern UI:** Tailwind CSS combined with Thymeleaf for a responsive, premium, visually attractive design like Cars.com or AutoTrader. Includes hover effects, gradients, and dynamic layouts.
- **Database:** Configured to use a file-based H2 database out-of-the-box (`jdbc:h2:file:./data/automart_db`), which satisfies the file handling/SQL requirement and requires zero setup! Spring Data JPA automatically handles the SQL schema.

## How to Run in IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Select **File > Open** and choose this `secondhandcars` directory.
3. IntelliJ will detect the `pom.xml` and download all required Maven dependencies automatically.
4. Once indexing is complete, open `src/main/java/com/automart/AutoMartApplication.java`.
5. Click the green **Run** arrow next to the `main` method.
6. Open your browser and go to: `http://localhost:8080`

## Pre-Loaded Test Data
The application automatically pre-loads some dummy data on its first run so you can see the UI in action.

**Admin Login:**
- **Email:** `admin@automart.com`
- **Password:** `admin123`

**Customer Login:**
- **Email:** `john@email.com`
- **Password:** `password`
