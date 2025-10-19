package zm.zra.zra_digital_fortress_backend.constant;

public class MessageConstants {

    private MessageConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String REGISTRATION_SUCCESS = "Registration successful. Please verify your email to activate your account.";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String LOGOUT_SUCCESS = "Logout successful";
    public static final String EMAIL_VERIFIED = "Email verified successfully. Your account is now active.";
    public static final String PASSWORD_RESET_SENT = "Password reset instructions have been sent to your email.";
    public static final String TOKEN_REFRESHED = "Token refreshed successfully";
    
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ACCOUNT_SUSPENDED = "Account is suspended. Please contact support.";
    public static final String ACCOUNT_CLOSED = "Account is closed.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered";
    public static final String PHONE_ALREADY_EXISTS = "Phone number already registered";
    public static final String NRC_ALREADY_EXISTS = "NRC number already registered";
    public static final String BUSINESS_REG_ALREADY_EXISTS = "Business registration number already exists";
    
    public static final String USER_NOT_FOUND = "User not found";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ACCESS_DENIED = "Access denied";
    
    public static final String TAX_FILING_SUCCESS = "Tax filing submitted successfully";
    public static final String TAX_FILING_DRAFT_SAVED = "Tax filing saved as draft";
    public static final String PAYMENT_SUCCESS = "Payment processed successfully";
    public static final String PAYMENT_FAILED = "Payment processing failed";
    
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String INVALID_MFA_CODE = "Invalid MFA code";
    public static final String MFA_REQUIRED = "MFA verification required";
    
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_ERROR = "An internal error occurred";
}