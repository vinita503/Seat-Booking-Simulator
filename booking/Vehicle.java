package booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Vehicle {

    private String vehicleId;
    private String vehicleName;
    private TransportType type;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private double fare;
    private List<Seat> seats;
    private int totalSeats;

    // Layout constants
    private static final SeatType[] TRAIN_ROW = {
            SeatType.WINDOW, SeatType.MIDDLE, SeatType.AISLE,
            SeatType.AISLE, SeatType.MIDDLE, SeatType.WINDOW
    };
    private static final SeatType[] BUS_ROW = {
            SeatType.WINDOW, SeatType.AISLE,
            SeatType.AISLE, SeatType.WINDOW
    };

    public Vehicle(String vehicleId, String vehicleName, TransportType type, String source, String destination, String departureTime, String arrivalTime, double fare, int totalSeats) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.totalSeats = totalSeats;
        this.seats = new ArrayList<>();
        generateSeats();
    }

    /** Constructor used when loading from file (with pre-built seats list) */
    public Vehicle(String vehicleId, String vehicleName, TransportType type, String source, String destination, String departureTime, String arrivalTime, double fare, int totalSeats, List<Seat> seats) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.totalSeats = totalSeats;
        this.seats = seats;
    }

    //Seat Generation 

    private void generateSeats() {
        SeatType[] layout = (type == TransportType.TRAIN) ? TRAIN_ROW : BUS_ROW;
        int seatsPerRow = layout.length;
        for (int i = 0; i < totalSeats; i++) {
            seats.add(new Seat(i + 1, layout[i % seatsPerRow]));
        }
    }

    //Seat Operations 

    public Optional<Seat> getSeat(int seatNumber) {
        return seats.stream().filter(s -> s.getSeatNumber() == seatNumber).findFirst();
    }

    public List<Seat> getAvailableSeats() {
        return seats.stream().filter(Seat::isAvailable).collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeatsByType(SeatType type) {
        return seats.stream().filter(s -> s.isAvailable() && s.getSeatType() == type).collect(Collectors.toList());
    }

    public int getAvailableCount() {
        return (int) seats.stream().filter(Seat::isAvailable).count();
    }

    public int getBookedCount() {
        return totalSeats - getAvailableCount();
    }

    //Seat Map Display 

    public void printSeatMap() {
        int seatsPerRow = (type == TransportType.TRAIN) ? 6 : 4;
        int half = seatsPerRow / 2;

        System.out.println("\n  Seat Map — " + vehicleName + " (" + type + ")");
        System.out.println("  Legend: [X]=Booked  W=Window  M=Middle  A=Aisle");
        System.out.println("  " + "─".repeat(50));

        // Column headers
        if (type == TransportType.TRAIN) {
            System.out.println("  Row   W   M   A  |  A   M   W");
        } 
        else {
            System.out.println("  Row   W   A  |  A   W");
        }
        System.out.println("  " + "─".repeat(50));

        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);
        for (int row = 0; row < totalRows; row++) {
            System.out.printf("  %3d  ", row + 1);
            for (int col = 0; col < seatsPerRow; col++) {
                int seatIdx = row * seatsPerRow + col;
                if (seatIdx < seats.size()) {
                    System.out.print(seats.get(seatIdx).displayCell());
                } else {
                    System.out.print("   "); // empty filler
                }
                if (col == half - 1)
                    System.out.print(" | "); // aisle gap
            }
            System.out.println();
        }
        System.out.println("  " + "─".repeat(50));
        System.out.printf("  Available: %d  |  Booked: %d  |  Total: %d%n",
                getAvailableCount(), getBookedCount(), totalSeats);
    }

    //Getters 

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public TransportType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public double getFare() {
        return fare;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public String toString() {
        return String.format("[%s] %-22s | %s → %s | Dep: %s Arr: %s | Rs.%.0f | %d/%d seats free",
                vehicleId, vehicleName, source, destination,
                departureTime, arrivalTime, fare,
                getAvailableCount(), totalSeats);
    }
}
