package booking;

import java.util.*;
import java.util.stream.Collectors;

public class BookingManager {

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();

    // Initialise

    public void initialise() {
        loadSampleVehicles();
        FileManager.applySeatStates(vehicles);
        bookings = FileManager.loadBookings();
    }

    private void loadSampleVehicles() {
        // Trains
        vehicles.add(new Vehicle("TR01", "Rajdhani Express", TransportType.TRAIN,
                "Delhi", "Mumbai", "06:00", "22:00", 1500.00, 60));
        vehicles.add(new Vehicle("TR02", "Shatabdi Express", TransportType.TRAIN,
                "Delhi", "Jaipur", "06:15", "10:45", 800.00, 48));
        vehicles.add(new Vehicle("TR03", "Duronto Express", TransportType.TRAIN,
                "Mumbai", "Pune", "07:00", "10:00", 450.00, 36));
        vehicles.add(new Vehicle("TR04", "Garib Rath", TransportType.TRAIN,
                "Kolkata", "Delhi", "18:00", "12:00", 700.00, 54));
        vehicles.add(new Vehicle("TR05", "Vande Bharat", TransportType.TRAIN,
                "Chennai", "Bengaluru", "06:00", "10:30", 1200.00, 48));

        // Buses
        vehicles.add(new Vehicle("BS01", "KSRTC Airavat Volvo", TransportType.BUS,
                "Bengaluru", "Mysuru", "07:00", "09:30", 350.00, 40));
        vehicles.add(new Vehicle("BS02", "MSRTC Shivneri", TransportType.BUS,
                "Mumbai", "Pune", "06:30", "09:00", 300.00, 36));
        vehicles.add(new Vehicle("BS03", "APSRTC Garuda", TransportType.BUS,
                "Hyderabad", "Vijayawada", "20:00", "04:00", 550.00, 40));
        vehicles.add(new Vehicle("BS04", "RSRTC Volvo", TransportType.BUS,
                "Jaipur", "Delhi", "22:00", "05:30", 650.00, 36));
        vehicles.add(new Vehicle("BS05", "HRTC Volvo", TransportType.BUS,
                "Delhi", "Shimla", "21:00", "06:00", 750.00, 40));
    }

    private void persist() {
        FileManager.saveSeatStates(vehicles);
        FileManager.saveBookings(bookings);
    }

    // Search

    public List<Vehicle> searchVehicles(String source, String destination, TransportType type) {
        return vehicles.stream()
                .filter(v -> v.getSource().equalsIgnoreCase(source))
                .filter(v -> v.getDestination().equalsIgnoreCase(destination))
                .filter(v -> type == null || v.getType() == type)
                .filter(v -> v.getAvailableCount() > 0)
                .collect(Collectors.toList());
    }

    public List<Vehicle> getAllVehicles(TransportType type) {
        return vehicles.stream()
                .filter(v -> type == null || v.getType() == type)
                .collect(Collectors.toList());
    }

    public Optional<Vehicle> getVehicle(String vehicleId) {
        return vehicles.stream()
                .filter(v -> v.getVehicleId().equalsIgnoreCase(vehicleId))
                .findFirst();
    }

    // Booking 

    public Booking bookSeat(String vehicleId, int seatNumber,SeatType preferredType,String name, String phone) {

        Optional<Vehicle> optVehicle = getVehicle(vehicleId);
        if (optVehicle.isEmpty()) {
            System.out.println(" Vehicle not found: " + vehicleId);
            return null;
        }
        Vehicle vehicle = optVehicle.get();

        Seat seat;
        if (seatNumber > 0) {
            // Specific seat requested
            Optional<Seat> optSeat = vehicle.getSeat(seatNumber);
            if (optSeat.isEmpty()) {
                System.out.println("Seat " + seatNumber + " does not exist.");
                return null;
            }
            seat = optSeat.get();
            if (seat.isBooked()) {
                System.out.println("Seat " + seatNumber + " is already booked.");
                return null;
            }
        } 
        else {
            // Auto-assign: try preferred type first, then any available
            List<Seat> pool = (preferredType != null)
                    ? vehicle.getAvailableSeatsByType(preferredType)
                    : vehicle.getAvailableSeats();

            if (pool.isEmpty() && preferredType != null) {
                System.out.println("No " + preferredType + " seats available. Assigning any available seat.");
                pool = vehicle.getAvailableSeats();
            }
            if (pool.isEmpty()) {
                System.out.println("No seats available on this vehicle.");
                return null;
            }
            seat = pool.get(0);
        }

        Booking booking = new Booking(name, phone, vehicle, seat);
        seat.book(name, booking.getBookingId());
        bookings.add(booking);
        persist();
        return booking;
    }

    //Cancellation 

    public boolean cancelBooking(String bookingId) {
        Optional<Booking> optBooking = bookings.stream()
                .filter(b -> b.getBookingId().equalsIgnoreCase(bookingId)
                        && !b.isCancelled())
                .findFirst();

        if (optBooking.isEmpty())
            return false;

        Booking booking = optBooking.get();
        booking.cancel();

        // Free the seat on the vehicle
        getVehicle(booking.getVehicleId())
                .flatMap(v -> v.getSeat(booking.getSeatNumber()))
                .ifPresent(Seat::cancel);

        persist();
        return true;
    }

    //Booking Queries 

    public List<Booking> getBookingsByPassenger(String name) {
        return bookings.stream()
                .filter(b -> b.getPassengerName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByPhone(String phone) {
        return bookings.stream()
                .filter(b -> b.getPassengerPhone().equals(phone))
                .collect(Collectors.toList());
    }

    public Optional<Booking> getBookingById(String id) {
        return bookings.stream()
                .filter(b -> b.getBookingId().equalsIgnoreCase(id))
                .findFirst();
    }

    public List<Booking> getAllActiveBookings() {
        return bookings.stream()
                .filter(b -> !b.isCancelled())
                .collect(Collectors.toList());
    }

    //Stats 

    public long getTotalBooked() {
        return bookings.stream().filter(b -> !b.isCancelled()).count();
    }

    public long getTotalCancelled() {
        return bookings.stream().filter(Booking::isCancelled).count();
    }

    public double getTotalRevenue() {
        return bookings.stream()
                .filter(b -> !b.isCancelled())
                .mapToDouble(Booking::getFare).sum();
    }
}

