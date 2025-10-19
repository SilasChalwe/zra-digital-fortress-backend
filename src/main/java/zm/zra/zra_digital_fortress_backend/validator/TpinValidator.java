package zm.zra.zra_digital_fortress_backend.validator;

import java.util.regex.Pattern;

public class TpinValidator {

    private static final Pattern TPIN_PATTERN = Pattern.compile("^\\d{9}[A-Z]$");

    private TpinValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValid(String tpin) {
        if (tpin == null || tpin.isEmpty()) {
            return false;
        }
        return TPIN_PATTERN.matcher(tpin).matches();
    }

    public static String sanitize(String tpin) {
        if (tpin == null) {
            return null;
        }
        return tpin.trim().toUpperCase();
    }

    public static void validate(String tpin) {
        if (!isValid(tpin)) {
            throw new IllegalArgumentException("Invalid TPIN format. Expected format: 9 digits followed by 1 uppercase letter (e.g., 123456789A)");
        }
    }
}