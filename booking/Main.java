package booking;

import java.util.List;
import java.util.Optional;

public class Main {
    private static final BookingManager manager = new BookingManager();

    public static void main(String[] args) {
        FileManager.ensureDataDir();
        manager.initialise();
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputHelper.readChoice("Enter choice: ", 1, 8);
            switch (choice) {
                case 1 -> searchAndBook();
                case 2 -> cancelBooking();
                case 3 -> viewSeatMap();
                case 4 -> viewAllVehicles();
                case 5 -> viewMyBookings();
                case 6 -> viewBookingById();
                case 7 -> showStats();
                case 8 -> {
                    System.out.println("Thank you for booking with us! Safe travels.");
                    running = false;
                }
            }
        }
    }

    //Banner & Menu 

    private static void printBanner() {
    System.out.println("TRAIN / BUS SEAT BOOKING SIMULATOR");
    System.out.println("Book smarter, travel better");
    System.out.println();
    System.out.printf("Active bookings loaded: %d\n\n", manager.getTotalBooked());
}

    private static void printMenu() {
    System.out.println("\nMAIN MENU");
    System.out.println("1. Search & Book a Seat");
    System.out.println("2. Cancel a Booking");
    System.out.println("3. View Seat Map");
    System.out.println("4. View All Trains / Buses");
    System.out.println("5. View My Bookings");
    System.out.println("6. View Booking by ID");
    System.out.println("7. Statistics");
    System.out.println("8. Exit");
}

    //Feature 1: Search & Book 

    private static void searchAndBook() {
        System.out.println("\nSEARCH & BOOK");

        String source = InputHelper.readNonEmpty("  From (city): ");
        String dest = InputHelper.readNonEmpty("  To   (city): ");

        System.out.println("  Transport type:");
        System.out.println("  1. Train   2. Bus   3. Both");
        int typeChoice = InputHelper.readChoice("  Choice: ", 1, 3);
        TransportType type = switch (typeChoice) {
            case 1 -> TransportType.TRAIN;
            case 2 -> TransportType.BUS;
            default -> null;
        };

        List<Vehicle> found = manager.searchVehicles(source, dest, type);

        if (found.isEmpty()) {
            System.out.println("\n  ✘ No vehicles found for " + source + " → " + dest
                    + (type != null ? " (" + type + ")" : "") + ".");
            System.out.println("  Tip: Check spelling. Available cities: Delhi, Mumbai, Pune,");
            System.out.println("       Bengaluru, Mysuru, Jaipur, Chennai, Hyderabad, Kolkata, Shimla, Vijayawada.");
            return;
        }

        System.out.println("\n  Found " + found.size() + " vehicle(s):\n");
        printDivider();
        System.out.printf("  %-4s %-22s %-8s %-6s %-6s %-10s %-6s%n",
                "#", "Vehicle", "Type", "Dep", "Arr", "Fare", "Avail");
        printDivider();
        for (int i = 0; i < found.size(); i++) {
            Vehicle v = found.get(i);
            System.out.printf("  %-4d %-22s %-8s %-6s %-6s Rs.%-7.0f %-6d%n",
                    i + 1, v.getVehicleName(), v.getType(),
                    v.getDepartureTime(), v.getArrivalTime(),
                    v.getFare(), v.getAvailableCount());
        }
        printDivider();

        int pick = InputHelper.readChoice("\n  Select vehicle (1-" + found.size() + "): ",
                1, found.size());
        Vehicle selected = found.get(pick - 1);

        // Show seat map
        selected.printSeatMap();

        // Seat selection
        System.out.println("\n  Seat Selection:");
        System.out.println("  1. Choose a specific seat number");
        System.out.println("  2. Auto-assign (choose preferred type)");
        int seatMode = InputHelper.readChoice("  Choice: ", 1, 2);

        int seatNumber = -1;
        SeatType preferred = null;

        if (seatMode == 1) {
            seatNumber = InputHelper.readInt("  Enter seat number: ");
        } else {
            System.out.println("  Preferred seat type:");
            System.out.println("  1. Window   2. Middle   3. Aisle   4. No preference");
            int prefChoice = InputHelper.readChoice("  Choice: ", 1, 4);
            preferred = switch (prefChoice) {
                case 1 -> SeatType.WINDOW;
                case 2 -> SeatType.MIDDLE;
                case 3 -> SeatType.AISLE;
                default -> null;
            };
        }

        // Passenger details
        System.out.println("\n  Passenger Details:");
        String name = InputHelper.readNonEmpty("  Full Name : ");
        String phone = InputHelper.readPhone("  Phone No. : ");

        Booking booking = manager.bookSeat(selected.getVehicleId(),
                seatNumber, preferred, name, phone);

        if (booking != null) {
            System.out.println("\n Booking Confirmed!");
            booking.printTicket();
        }
    }

    //Feature 2: Cancel Booking 

    private static void cancelBooking() {
        System.out.println("\nCANCEL BOOKING");
        String id = InputHelper.readNonEmpty("  Enter Booking ID (e.g. BK1001): ");

        Optional<Booking> opt = manager.getBookingById(id);
        if (opt.isEmpty()) {
            System.out.println("Booking not found: " + id);
            return;
        }

        Booking b = opt.get();
        if (b.isCancelled()) {
            System.out.println("This booking is already cancelled.");
            return;
        }

        System.out.println("\nBooking to cancel:");
        b.printTicket();

        String confirm = InputHelper.readLine("\n  Confirm cancellation? (yes/no): ");
        if (confirm.equalsIgnoreCase("yes")) {
            if (manager.cancelBooking(id)) {
                System.out.println("Booking " + id + " has been cancelled. Seat is now available.");
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    //Feature 3: View Seat Map 

    private static void viewSeatMap() {
        System.out.println("\nVIEW SEAT MAP");
        viewAllVehicles();
        String vehicleId = InputHelper.readNonEmpty("\nEnter Vehicle ID (e.g. TR01): ");

        manager.getVehicle(vehicleId).ifPresentOrElse(
                Vehicle::printSeatMap,
                () -> System.out.println("Vehicle not found: " + vehicleId));
    }

    //Feature 4: View All Vehicles 

    private static void viewAllVehicles() {
        System.out.println("\nALL VEHICLES");
        System.out.println("  Filter:  1. All   2. Trains only   3. Buses only");
        int f = InputHelper.readChoice("  Choice: ", 1, 3);
        TransportType filter = switch (f) {
            case 2 -> TransportType.TRAIN;
            case 3 -> TransportType.BUS;
            default -> null;
        };

        List<Vehicle> list = manager.getAllVehicles(filter);
        printDivider();
        System.out.printf("  %-5s %-22s %-7s %-12s %-14s %-6s %-6s %-6s%n",
                "ID", "Name", "Type", "Route", "", "Dep", "Fare", "Free");
        printDivider();
        for (Vehicle v : list) {
            System.out.printf("  %-5s %-22s %-7s %-12s→%-13s %-6s Rs.%-4.0f %-6d%n",
                    v.getVehicleId(), v.getVehicleName(), v.getType(),
                    v.getSource(), v.getDestination(),
                    v.getDepartureTime(), v.getFare(), v.getAvailableCount());
        }
        printDivider();
    }

    //Feature 5: View My Bookings 

    private static void viewMyBookings() {
        System.out.println("\nMY BOOKINGS");
        System.out.println("  Search by:  1. Name   2. Phone");
        int mode = InputHelper.readChoice("  Choice: ", 1, 2);

        List<Booking> results;
        if (mode == 1) {
            String name = InputHelper.readNonEmpty("  Enter passenger name: ");
            results = manager.getBookingsByPassenger(name);
        } else {
            String phone = InputHelper.readPhone("  Enter phone number: ");
            results = manager.getBookingsByPhone(phone);
        }

        if (results.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        System.out.println("\nFound " + results.size() + " booking(s):\n");
        printDivider();
        results.forEach(b -> System.out.println("  " + b));
        printDivider();

        System.out.println("\nPrint full ticket for any booking? (Enter ID or 'no'): ");
        String id = InputHelper.readLine("  > ");
        if (!id.equalsIgnoreCase("no")) {
            results.stream()
                    .filter(b -> b.getBookingId().equalsIgnoreCase(id))
                    .findFirst()
                    .ifPresentOrElse(
                            Booking::printTicket,
                            () -> System.out.println("Booking not found in results."));
        }
    }

    //Feature 6: View Booking By ID 

    private static void viewBookingById() {
        System.out.println("\nVIEW BOOKING");
        String id = InputHelper.readNonEmpty("Enter Booking ID: ");
        manager.getBookingById(id).ifPresentOrElse(
                Booking::printTicket,
                () -> System.out.println("Booking not found: " + id));
    }

    //Feature 7: Statistics 

    private static void showStats() {
        System.out.println("\nSTATISTICS ");
        printDivider();
        System.out.printf("  Total Active Bookings : %d%n", manager.getTotalBooked());
        System.out.printf("  Total Cancellations   : %d%n", manager.getTotalCancelled());
        System.out.printf("  Total Revenue         : Rs.%.2f%n", manager.getTotalRevenue());
        printDivider();

        System.out.println("\n  Vehicle Occupancy:");
        printDivider();
        System.out.printf("  %-5s %-22s %-7s %6s %6s %6s%n",
                "ID", "Name", "Type", "Total", "Booked", "Free");
        printDivider();
        manager.getAllVehicles(null).forEach(v -> System.out.printf("  %-5s %-22s %-7s %6d %6d %6d%n",
                v.getVehicleId(), v.getVehicleName(), v.getType(),
                v.getTotalSeats(), v.getBookedCount(), v.getAvailableCount()));
        printDivider();
    }

    //Helper

    private static void printDivider() {
        System.out.println("  " + "─".repeat(72));
    }
}
