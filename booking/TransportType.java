package booking;

public enum TransportType {
    TRAIN, BUS;
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
