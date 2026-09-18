package booking;

public enum SeatType {
    WINDOW,  
    MIDDLE,  
    AISLE;    

    public String label() {
        return switch (this) {
            case WINDOW -> "W";
            case MIDDLE -> "M";
            case AISLE  -> "A";
        };
    }
}
