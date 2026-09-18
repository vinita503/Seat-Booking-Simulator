package booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Booking {

    private static int counter = 1000;

    private String bookingId;
    private String passengerName;
    private String passengerPhone;
    private String vehicleId;
    private String vehicleName;
    private TransportType transportType;
    private String source;
    private String destination;
    private String departureTime;
    private int seatNumber;
    private SeatType seatType;
    private double fare;
    private String bookedAt;
    private boolean cancelled;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Booking(String passengerName, String passengerPhone, Vehicle vehicle, Seat seat) {
        this.bookingId = "BK" + (++counter);
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.vehicleId = vehicle.getVehicleId();
        this.vehicleName = vehicle.getVehicleName();
        this.transportType = vehicle.getType();
        this.source = vehicle.getSource();
        this.destination = vehicle.getDestination();
        this.departureTime = vehicle.getDepartureTime();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.fare = vehicle.getFare();
        this.bookedAt = LocalDateTime.now().format(FMT);
        this.cancelled = false;
    }

    /** Constructor used when loading from CSV file */
    public Booking(String bookingId, String passengerName, String passengerPhone, String vehicleId, String vehicleName,
            TransportType transportType, String source, String destination, String departureTime, int seatNumber,
            SeatType seatType, double fare, String bookedAt, boolean cancelled) {
        this.bookingId = bookingId;
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.transportType = transportType;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.fare = fare;
        this.bookedAt = bookedAt;
        this.cancelled = cancelled;

        // Sync counter
        try {
            int num = Integer.parseInt(bookingId.substring(2));
            if (num >= counter)
                counter = num + 1;
        } catch (Exception ignored) {
        }
    }

    // Getters

    public String getBookingId() {
        return bookingId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public TransportType getTransportType() {
        return transportType;
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

    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public double getFare() {
        return fare;
    }

    public String getBookedAt() {
        return bookedAt;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    // Display

    public void printTicket() {
        System.out.println(cancelled ? "CANCELLED" : "BOOKING CONFIRMATION");
        System.out.println();

        System.out.printf("Booking ID   : %s%n", bookingId);
        System.out.printf("Passenger    : %s%n", passengerName);
        System.out.printf("Phone        : %s%n", passengerPhone);
        System.out.printf("Type         : %s%n", transportType);
        System.out.printf("Vehicle      : %s%n", vehicleName);
        System.out.printf("Route        : %s%n", source + " → " + destination);
        System.out.printf("Departure    : %s%n", departureTime);
        System.out.printf("Seat No.     : %s%n", seatNumber);
        System.out.printf("Seat Type    : %s%n", seatType);
        System.out.printf("Fare         : Rs. %.2f%n", fare);
        System.out.printf("Booked At    : %s%n", bookedAt);
    }

    public String toString() {
        String status = cancelled ? "[CANCELLED]" : "[ACTIVE]";
        return String.format("%s %s | %s | %s→%s | Seat %d [%s] | Rs.%.0f",
                status, bookingId, passengerName,
                source, destination, seatNumber, seatType.label(), fare);
    }
}
