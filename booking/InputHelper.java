package booking;

import java.util.Scanner;

public class InputHelper {

    private static final Scanner sc = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static int readChoice(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v >= min && v <= max)
                return v;
            System.out.printf("Enter a number between %d and %d.%n", min, max);
        }
    }

    public static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (s.matches("\\d{10}"))
                return s;
            System.out.println("Enter a valid 10-digit phone number.");
        }
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty())
                return s;
            System.out.println("This field cannot be empty.");
        }
    }
}
