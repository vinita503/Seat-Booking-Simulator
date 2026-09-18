# Train/Bus Seat Booking Simulator (CLI) — Java

A command-line Java application that simulates a real-world transport seat booking system. Supports both **trains and buses**, with a visual seat map (Window / Middle / Aisle), booking, cancellation, route search, and full CSV-based persistence across sessions.

---

## Problem Statement

Booking seats in real transport systems involves complex logic — tracking seat availability, assigning seat types, managing cancellations, and maintaining booking records. This project simulates that system end-to-end in a pure Java CLI application, demonstrating how object-oriented design handles a multi-entity real-world problem.

---

## Features

| Feature | Description |
|---|---|
| Search by Route | Find trains/buses between two cities with availability |
| Book a Seat | Choose a specific seat or auto-assign by preferred type (Window/Middle/Aisle) |
| Cancel Booking | Cancel any active booking by ID with seat freed automatically |
| Seat Map | Visual ASCII seat map showing available and booked seats |
| All Vehicles | View all trains and buses with occupancy stats |
| My Bookings | Find all bookings by passenger name or phone |
| View Ticket | Print a full ticket by Booking ID |
| Statistics | Total bookings, cancellations, revenue, and occupancy per vehicle |
| Persistence | All bookings and seat states saved to CSV files |

---

## Project Structure

```
SeatBookingSimulator/ 
├── src/ 
│   └── booking/ 
│       ├── Main.java - Command Line Interface (CLI) menu that will be the overarching orchestrator for all the features of the simulator
│       ├── BookingManager.java - The core logic that governs requests pertaining to searching, booking, cancelling and statistics
│       ├── Vehicle.java - Represents either train or bus with seat layout creation for each type
│       ├── Seat.java - Represents one seat within a vehicle and provides details regarding the type of seat and whether its already been booked or not
│       ├── Booking.java - Represents the details related to a booking so display ticket information
│       ├── FileManager.java - Handles persistent storage of booking information in csv delimited format and aggregate booking state of seats in a csv delimited format
│       ├── InputHelper.java - Validates the user inputs from the console prior to submitting as a request
│       ├── SeatType.java - Enum for defining seat types of WINDOW, MIDDLE, or AISLE
│       └── TransportType.java - Enum for defining transportation methods of TRAIN or BUS
├── data/
│   ├── bookings.csv - Auto-generated at time of first booking 
│   └── seat_state.csv - Auto-generated at time of first booking
└── README.md
```

---

## Pre-loaded Routes

### Trains
| ID | Name | Route | Fare |
| TR01 | Rajdhani Express | Delhi → Mumbai | Rs.1500 |
| TR02 | Shatabdi Express | Delhi → Jaipur | Rs.800 |
| TR03 | Duronto Express | Mumbai → Pune | Rs.450 |
| TR04 | Garib Rath | Kolkata → Delhi | Rs.700 |
| TR05 | Vande Bharat | Chennai → Bengaluru | Rs.1200 |

### Buses
| ID | Name | Route | Fare |
| BS01 | KSRTC Airavat Volvo | Bengaluru → Mysuru | Rs.350 |
| BS02 | MSRTC Shivneri | Mumbai → Pune | Rs.300 |
| BS03 | APSRTC Garuda | Hyderabad → Vijayawada | Rs.550 |
| BS04 | RSRTC Volvo | Jaipur → Delhi | Rs.650 |
| BS05 | HRTC Volvo | Delhi → Shimla | Rs.750 |

---

## Seat Layout

**Train (6 seats per row):** `W M A | A M W`

**Bus (4 seats per row):** `W A | A W`

On the seat map: `[W]` = Booked Window, ` W ` = Available Window

---

## Java Concepts Used

| Concept | Where Applied |
| OOP & Encapsulation | All 7 model/logic classes |
| Enums with methods | `SeatType.label()`, `TransportType.toString()` |
| Collections (ArrayList) | Vehicles, seats, bookings |
| Optional\<T\> | `getVehicle()`, `getSeat()`, `getBookingById()` |
| Streams & Lambdas | Search, filter, sort, aggregate across all lists |
| File I/O (BufferedReader/Writer) | CSV persistence in FileManager |
| Exception Handling | Input validation, file errors |
| Switch Expressions | Transport type and seat type selection |
| String Formatting | Seat maps, ticket display, tables |
| java.time API | Booking timestamps |

---

## Prerequisites

- Java JDK 17 or above
- Any terminal / command prompt

---

## How to Run

```bash
git clone https://github.com/YOUR_USERNAME/SeatBookingSimulator.git
cd SeatBookingSimulator
mkdir out
javac -d out src/booking/*.java
java -cp out booking.Main
```

> A `data/` folder is auto-created. All bookings persist across sessions.

---

## Sample Seat Map (Train)

```
  Seat Map — Rajdhani Express (Train)
  Legend: [X]=Booked  W=Window  M=Middle  A=Aisle
  ──────────────────────────────────────────────────
  Row   W   M   A  |  A   M   W
  ──────────────────────────────────────────────────
    1   W   M  [A] |  A   M   W
    2   W   M   A  | [A]  M   W
    3  [W]  M   A  |  A   M  [W]
  ──────────────────────────────────────────────────
  Available: 54  |  Booked: 6  |  Total: 60
```

---

## Future Improvements

- Date-based booking (different schedules per day)
- Fare classes (Sleeper, 3AC, 2AC)
- Waitlist management
- JavaFX GUI for visual seat selection
- PDF ticket generation

---

## Author

Vinita Choudhary
B.Tech ECE, VIT Bhopal University
Course: Programming in Java
