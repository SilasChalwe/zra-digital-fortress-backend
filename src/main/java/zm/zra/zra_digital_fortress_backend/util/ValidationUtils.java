package zm.zra.zra_digital_fortress_backend.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+260[0-9]{9}$");

    private ValidationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isPositive(Double value) {
        return value != null && value > 0;
    }

    public static boolean isPositiveOrZero(Double value) {
        return value != null && value >= 0;
    }

    public static boolean isInRange(Integer value, int min, int max) {
        return value != null && value >= min && value <= max;
    }

    public static boolean isInRange(Double value, double min, double max) {
        return value != null && value >= min && value <= max;
    }

    public static String sanitizeString(String str) {
        return str != null ? str.trim() : null;
    }
}