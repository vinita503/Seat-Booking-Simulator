package booking;

public class Seat {

    private int seatNumber;
    private SeatType seatType;
    private boolean isBooked;
    private String bookedBy; 
    private String bookingId; 

    public Seat(int seatNumber, SeatType seatType) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = false;
        this.bookedBy = null;
        this.bookingId = null;
    }

    //Getters 

    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getBookingId() {
        return bookingId;
    }

    public boolean isAvailable() {
        return !isBooked;
    }

    //State Change 

    public void book(String passengerName, String bookingId) {
        this.isBooked = true;
        this.bookedBy = passengerName;
        this.bookingId = bookingId;
    }

    public void cancel() {
        this.isBooked = false;
        this.bookedBy = null;
        this.bookingId = null;
    }

    //Display

    public String displayCell() {
        String label = seatType.label();
        return isBooked ? "[" + label + "]" : " " + label + " ";
    }

    public String toString() {
        String status = isBooked? "BOOKED by " + bookedBy + " (ID: " + bookingId + ")": "AVAILABLE";
        return String.format("Seat %3d [%s] — %s", seatNumber, seatType.label(), status);
    }
}
