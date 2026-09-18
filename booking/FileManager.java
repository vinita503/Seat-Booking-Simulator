package booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String DATA_DIR = "data/";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.csv";
    private static final String SEAT_STATE_FILE = DATA_DIR + "seat_state.csv";

    public static void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();
    }

    // Bookings

    public static void saveBookings(List<Booking> bookings) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            bw.write("bookingId,passengerName,passengerPhone,vehicleId,vehicleName," +
                    "transportType,source,destination,departureTime,seatNumber," +
                    "seatType,fare,bookedAt,cancelled");
            bw.newLine();
            for (Booking b : bookings) {
                bw.write(String.join(",",
                        b.getBookingId(),
                        esc(b.getPassengerName()),
                        b.getPassengerPhone(),
                        b.getVehicleId(),
                        esc(b.getVehicleName()),
                        b.getTransportType().name(),
                        esc(b.getSource()),
                        esc(b.getDestination()),
                        b.getDepartureTime(),
                        String.valueOf(b.getSeatNumber()),
                        b.getSeatType().name(),
                        String.valueOf(b.getFare()),
                        b.getBookedAt(),
                        String.valueOf(b.isCancelled())));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BOOKINGS_FILE);
        if (!file.exists())
            return bookings;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.isBlank())
                    continue;
                String[] p = splitCSV(line);
                if (p.length < 14)
                    continue;
                bookings.add(new Booking(
                        p[0].trim(),
                        unesc(p[1]), unesc(p[2]),
                        p[3].trim(), unesc(p[4]),
                        TransportType.valueOf(p[5].trim()),
                        unesc(p[6]), unesc(p[7]),
                        p[8].trim(),
                        Integer.parseInt(p[9].trim()),
                        SeatType.valueOf(p[10].trim()),
                        Double.parseDouble(p[11].trim()),
                        p[12].trim(),
                        Boolean.parseBoolean(p[13].trim())));
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    // Seat States

    public static void saveSeatStates(List<Vehicle> vehicles) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SEAT_STATE_FILE))) {
            bw.write("vehicleId,seatNumber,isBooked,bookedBy,bookingId");
            bw.newLine();
            for (Vehicle v : vehicles) {
                for (Seat s : v.getSeats()) {
                    if (s.isBooked()) {
                        bw.write(String.join(",",
                                v.getVehicleId(),
                                String.valueOf(s.getSeatNumber()),
                                "true",
                                esc(s.getBookedBy()),
                                s.getBookingId()));
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving seat states: " + e.getMessage());
        }
    }

    public static void applySeatStates(List<Vehicle> vehicles) {
        File file = new File(SEAT_STATE_FILE);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.isBlank())
                    continue;
                String[] p = line.split(",", 5);
                if (p.length < 5)
                    continue;

                String vehicleId = p[0].trim();
                int seatNumber = Integer.parseInt(p[1].trim());
                String bookedBy = unesc(p[3]);
                String bookingId = p[4].trim();

                vehicles.stream()
                        .filter(v -> v.getVehicleId().equals(vehicleId))
                        .findFirst()
                        .flatMap(v -> v.getSeat(seatNumber))
                        .ifPresent(s -> s.book(bookedBy, bookingId));
            }
        } catch (IOException e) {
            System.out.println("Error loading seat states: " + e.getMessage());
        }
    }

    //CSV Helpers 

    private static String esc(String v) {
        if (v == null)
            return "";
        return v.contains(",") ? "\"" + v.replace("\"", "\"\"") + "\"" : v;
    }

    private static String unesc(String v) {
        if (v == null)
            return "";
        v = v.trim();
        if (v.startsWith("\"") && v.endsWith("\""))
            v = v.substring(1, v.length() - 1).replace("\"\"", "\"");
        return v;
    }

    private static String[] splitCSV(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}
