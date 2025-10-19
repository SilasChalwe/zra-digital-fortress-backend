package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zm.zra.zra_digital_fortress_backend.dto.request.LoginRequest;
import zm.zra.zra_digital_fortress_backend.dto.request.RegisterBusinessRequest;
import zm.zra.zra_digital_fortress_backend.dto.request.RegisterIndividualRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.AuthResponse;
import zm.zra.zra_digital_fortress_backend.exception.BadRequestException;
import zm.zra.zra_digital_fortress_backend.exception.UnauthorizedException;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.*;
import zm.zra.zra_digital_fortress_backend.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for handling authentication and user registration operations.
 * Provides methods for user registration, login, email verification, and token management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final IndividualTaxpayerRepository individualTaxpayerRepository;
    private final BusinessTaxpayerRepository businessTaxpayerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final TpinGeneratorService tpinGeneratorService;
    private final EmailService emailService;
    private final MfaService mfaService;
    private final AuditLogService auditLogService;

    /**
     * Registers a new individual taxpayer.
     * 
     * @param request Registration details for individual taxpayer
     * @return AuthResponse with user details and registration status
     * @throws BadRequestException if email, phone, or NRC already exists
     */
    @Transactional
    public AuthResponse registerIndividual(RegisterIndividualRequest request) {
        log.info("Registering new individual taxpayer: {}", request.getEmail());

        // Validate unique constraints
        validateUniqueEmail(request.getEmail());
        validateUniquePhone(request.getPhoneNumber());
        validateUniqueNrc(request.getNrcNumber());

        // Generate unique TPIN
        String tpin = tpinGeneratorService.generateTpin();
        log.debug("Generated TPIN: {} for email: {}", tpin, request.getEmail());

        // Create User entity - DON'T set ID manually
        User user = createUser(
            tpin,
            request.getEmail(),
            request.getPassword(),
            request.getPhoneNumber(),
            User.UserType.INDIVIDUAL
        );

        log.debug("User entity created with ID: {}", user.getId());

        // Create Individual Taxpayer Profile
        IndividualTaxpayer taxpayer = createIndividualTaxpayer(user, request);
        
        // Save everything in correct order
        User savedUser = userRepository.save(user);
        individualTaxpayerRepository.save(taxpayer);
        
        log.debug("Individual taxpayer profile created for user ID: {}", savedUser.getId());

        // Initialize Compliance Score
        initializeComplianceScore(savedUser);
        log.debug("Compliance score initialized");

        // Send verification email
        emailService.sendVerificationEmail(
            savedUser.getEmail(), 
            savedUser.getEmailVerificationToken(), 
            tpin
        );
        log.info("Verification email sent to: {}", savedUser.getEmail());

        // Log registration audit
        auditLogService.logAction(
            savedUser.getId(), 
            "USER_REGISTRATION", 
            AuditLog.EntityType.USER, 
            savedUser.getId(), 
            "Individual taxpayer registered"
        );

        log.info("Individual taxpayer registered successfully with TPIN: {} and ID: {}", tpin, savedUser.getId());

        return AuthResponse.builder()
                .accessToken(null)
                .user(buildIndividualUserResponse(savedUser, taxpayer))
                .mfaRequired(false)
                .build();
    }

    /**
     * Registers a new business taxpayer.
     * 
     * @param request Registration details for business taxpayer
     * @return AuthResponse with user details and registration status
     * @throws BadRequestException if email, phone, or registration number already exists
     */
    @Transactional
    public AuthResponse registerBusiness(RegisterBusinessRequest request) {
        log.info("Registering new business taxpayer: {}", request.getEmail());

        // Validate unique constraints
        validateUniqueEmail(request.getEmail());
        validateUniquePhone(request.getPhoneNumber());
        validateUniqueBusinessRegistration(request.getRegistrationNumber());

        // Generate unique TPIN
        String tpin = tpinGeneratorService.generateTpin();
        log.debug("Generated TPIN: {} for business: {}", tpin, request.getBusinessName());

        // Create User entity - DON'T set ID manually
        User user = createUser(
            tpin,
            request.getEmail(),
            request.getPassword(),
            request.getPhoneNumber(),
            User.UserType.BUSINESS
        );

        log.debug("User entity created with ID: {}", user.getId());

        // Create Business Taxpayer Profile
        BusinessTaxpayer taxpayer = createBusinessTaxpayer(user, request);
        
        // Save everything in correct order
        User savedUser = userRepository.save(user);
        businessTaxpayerRepository.save(taxpayer);
        log.debug("Business taxpayer profile created for user ID: {}", savedUser.getId());

        // Initialize Compliance Score
        initializeComplianceScore(savedUser);
        log.debug("Compliance score initialized");

        // Send verification email
        emailService.sendVerificationEmail(
            savedUser.getEmail(), 
            savedUser.getEmailVerificationToken(), 
            tpin
        );
        log.info("Verification email sent to: {}", savedUser.getEmail());

        // Log registration audit
        auditLogService.logAction(
            savedUser.getId(), 
            "USER_REGISTRATION", 
            AuditLog.EntityType.USER, 
            savedUser.getId(), 
            "Business taxpayer registered: " + request.getBusinessName()
        );

        log.info("Business taxpayer registered successfully with TPIN: {} and ID: {}", tpin, savedUser.getId());

        return AuthResponse.builder()
                .user(buildBusinessUserResponse(savedUser, taxpayer))
                .mfaRequired(false)
                .build();
    }

    /**
     * Authenticates a user and generates JWT tokens.
     * 
     * @param request Login credentials (TPIN/email and password)
     * @param ipAddress IP address of the login request
     * @return AuthResponse with JWT tokens if successful
     * @throws BadRequestException if credentials are invalid or account is suspended
     * @throws UnauthorizedException if MFA validation fails
     */
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        log.info("Login attempt for: {}", request.getTpinOrEmail());

        // Find user by TPIN or email
        User user = findUserByTpinOrEmail(request.getTpinOrEmail());

        // Validate account status
        validateAccountStatus(user);

        // Authenticate user
        Authentication authentication = authenticateUser(user.getEmail(), request.getPassword());

        // Handle MFA if enabled
        if (user.getMfaEnabled()) {
            return handleMfaLogin(user, request.getMfaCode(), authentication);
        }

        // Generate tokens
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        // Update last login timestamp
        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        // Log successful login
        auditLogService.logAction(
            savedUser.getId(), 
            "USER_LOGIN", 
            AuditLog.EntityType.USER, 
            savedUser.getId(), 
            "User logged in from " + ipAddress
        );

        log.info("User logged in successfully: {} with ID: {}", savedUser.getTpin(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(buildUserResponseFromUser(savedUser))
                .mfaRequired(false)
                .build();
    }

    /**
     * Verifies a user's email address using the verification token.
     * 
     * @param token Email verification token
     * @return AuthResponse with updated user status
     * @throws BadRequestException if token is invalid or expired
     */
    @Transactional
    public AuthResponse verifyEmail(String token) {
        log.info("Email verification attempted with token: {}", token.substring(0, 8) + "...");

        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired verification token"));

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setStatus(User.AccountStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        auditLogService.logAction(
            savedUser.getId(), 
            "EMAIL_VERIFIED", 
            AuditLog.EntityType.USER, 
            savedUser.getId(), 
            "Email verified successfully"
        );

        log.info("Email verified successfully for user: {} with ID: {}", savedUser.getTpin(), savedUser.getId());

        return AuthResponse.builder()
                .user(buildUserResponseFromUser(savedUser))
                .build();
    }

    /**
     * Generates a password reset token and sends reset email.
     * 
     * @param email User's email address
     * @return Password reset token
     * @throws BadRequestException if email not found
     */
    @Transactional
    public String generatePasswordResetToken(String email) {
        log.info("Password reset requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found with email: " + email));

        String resetToken = UUID.randomUUID().toString();
        
        // Send password reset email
        emailService.sendPasswordResetEmail(email, resetToken);

        auditLogService.logAction(
            user.getId(), 
            "PASSWORD_RESET_REQUESTED", 
            AuditLog.EntityType.USER, 
            user.getId(), 
            "Password reset requested"
        );

        log.info("Password reset email sent to: {}", email);

        return resetToken;
    }

    /**
     * Refreshes JWT access token using a valid refresh token.
     * 
     * @param refreshToken Valid refresh token
     * @return AuthResponse with new access and refresh tokens
     * @throws BadRequestException if refresh token is invalid
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Token refresh requested");

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, null);

        String newAccessToken = tokenProvider.generateToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        log.info("Tokens refreshed successfully for user: {} with ID: {}", user.getTpin(), user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(buildUserResponseFromUser(user))
                .build();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Validates that email is unique in the system.
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration attempt with existing email: {}", email);
            throw new BadRequestException("Email already registered");
        }
    }

    /**
     * Validates that phone number is unique in the system.
     */
    private void validateUniquePhone(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            log.warn("Registration attempt with existing phone: {}", phoneNumber);
            throw new BadRequestException("Phone number already registered");
        }
    }

    /**
     * Validates that NRC number is unique in the system.
     */
    private void validateUniqueNrc(String nrcNumber) {
        if (individualTaxpayerRepository.existsByNrcNumber(nrcNumber)) {
            log.warn("Registration attempt with existing NRC: {}", nrcNumber);
            throw new BadRequestException("NRC number already registered");
        }
    }

    /**
     * Validates that business registration number is unique.
     */
    private void validateUniqueBusinessRegistration(String registrationNumber) {
        if (businessTaxpayerRepository.existsByRegistrationNumber(registrationNumber)) {
            log.warn("Registration attempt with existing business reg: {}", registrationNumber);
            throw new BadRequestException("Business registration number already exists");
        }
    }

    /**
     * Creates a new User entity with common fields.
     * IMPORTANT: Don't set ID manually when using @GeneratedValue
     */
    private User createUser(String tpin, String email, String password, 
                           String phoneNumber, User.UserType userType) {
        return User.builder()
                // REMOVED: .id(UUID.randomUUID().toString()) - Let Hibernate generate the ID
                .tpin(tpin)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .userType(userType)
                .status(User.AccountStatus.PENDING)
                .mfaEnabled(false)
                .emailVerified(false)
                .phoneVerified(false)
                .emailVerificationToken(UUID.randomUUID().toString())
                .build();
    }

    /**
     * Creates an IndividualTaxpayer profile from registration request.
     */
    private IndividualTaxpayer createIndividualTaxpayer(User user, RegisterIndividualRequest request) {
        return IndividualTaxpayer.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .dateOfBirth(request.getDateOfBirth())
                .nrcNumber(request.getNrcNumber())
                .physicalAddress(request.getPhysicalAddress())
                .postalAddress(request.getPostalAddress())
                .gender(IndividualTaxpayer.Gender.valueOf(request.getGender()))
                .employmentStatus(IndividualTaxpayer.EmploymentStatus.valueOf(request.getEmploymentStatus()))
                .employerName(request.getEmployerName())
                .employerTpin(request.getEmployerTpin())
                .estimatedAnnualIncome(request.getEstimatedAnnualIncome())
                .build();
    }

    /**
     * Creates a BusinessTaxpayer profile from registration request.
     */
    private BusinessTaxpayer createBusinessTaxpayer(User user, RegisterBusinessRequest request) {
        return BusinessTaxpayer.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .businessType(BusinessTaxpayer.BusinessType.valueOf(request.getBusinessType()))
                .registrationNumber(request.getRegistrationNumber())
                .dateOfIncorporation(request.getDateOfIncorporation())
                .businessAddress(request.getBusinessAddress())
                .postalAddress(request.getPostalAddress())
                .contactPersonName(request.getContactPersonName())
                .contactPersonPhone(request.getContactPersonPhone())
                .contactPersonEmail(request.getContactPersonEmail())
                .numberOfEmployees(request.getNumberOfEmployees())
                .sector(BusinessTaxpayer.BusinessSector.valueOf(request.getSector()))
                .estimatedAnnualTurnover(request.getEstimatedAnnualTurnover())
                .vatRegistered(request.getVatRegistered() != null ? request.getVatRegistered() : false)
                .vatRegistrationNumber(request.getVatRegistrationNumber())
                .build();
    }

    /**
     * Initializes compliance score for a new user.
     */
    private void initializeComplianceScore(User user) {
        ComplianceScore complianceScore = ComplianceScore.builder()
                .user(user)
                .overallScore(0)
                .timelyFilingScore(0)
                .accuracyScore(0)
                .paymentHistoryScore(0)
                .engagementScore(0)
                .totalFilings(0)
                .onTimeFilings(0)
                .lateFilings(0)
                .totalPayments(0)
                .onTimePayments(0)
                .latePayments(0)
                .consecutiveOnTimeFilings(0)
                .badges("[]")
                .build();

        user.setComplianceScore(complianceScore);
        userRepository.save(user);
    }

    /**
     * Finds user by TPIN or email.
     */
    private User findUserByTpinOrEmail(String tpinOrEmail) {
        return userRepository.findByTpin(tpinOrEmail)
                .or(() -> userRepository.findByEmail(tpinOrEmail))
                .orElseThrow(() -> {
                    log.warn("Login attempt with invalid credentials: {}", tpinOrEmail);
                    return new BadRequestException("Invalid credentials");
                });
    }

    /**
     * Validates that user account is in good standing.
     */
    private void validateAccountStatus(User user) {
        if (user.getStatus() == User.AccountStatus.SUSPENDED) {
            log.warn("Login attempt for suspended account: {}", user.getTpin());
            throw new BadRequestException("Account is suspended. Please contact support.");
        }

        if (user.getStatus() == User.AccountStatus.CLOSED) {
            log.warn("Login attempt for closed account: {}", user.getTpin());
            throw new BadRequestException("Account is closed.");
        }
    }

    /**
     * Authenticates user credentials.
     */
    private Authentication authenticateUser(String email, String password) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", email, e);
            throw new BadRequestException("Invalid credentials");
        }
    }

    /**
     * Handles MFA validation during login.
     */
    private AuthResponse handleMfaLogin(User user, String mfaCode, Authentication authentication) {
        if (mfaCode == null || mfaCode.isEmpty()) {
            log.info("MFA required for user: {}", user.getTpin());
            return AuthResponse.builder()
                    .mfaRequired(true)
                    .user(buildUserResponseFromUser(user))
                    .build();
        }

        // Validate MFA code
        boolean mfaValid = mfaService.validateMfaCode(user.getMfaSecret(), mfaCode);
        if (!mfaValid) {
            log.warn("Invalid MFA code for user: {}", user.getTpin());
            throw new UnauthorizedException("Invalid MFA code");
        }

        // Generate tokens after successful MFA
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        user.setLastLogin(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        log.info("User logged in successfully with MFA: {} with ID: {}", savedUser.getTpin(), savedUser.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(buildUserResponseFromUser(savedUser))
                .mfaRequired(false)
                .build();
    }

    /**
     * Builds AuthResponse.UserResponse for individual taxpayer.
     */
    private AuthResponse.UserResponse buildIndividualUserResponse(User user, IndividualTaxpayer taxpayer) {
        return AuthResponse.UserResponse.builder()
                .id(user.getId())
                .tpin(user.getTpin())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name())
                .accountStatus(user.getStatus().name())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .firstName(taxpayer.getFirstName())
                .lastName(taxpayer.getLastName())
                .build();
    }

    /**
     * Builds AuthResponse.UserResponse for business taxpayer.
     */
    private AuthResponse.UserResponse buildBusinessUserResponse(User user, BusinessTaxpayer taxpayer) {
        return AuthResponse.UserResponse.builder()
                .id(user.getId())
                .tpin(user.getTpin())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name())
                .accountStatus(user.getStatus().name())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .businessName(taxpayer.getBusinessName())
                .build();
    }

    /**
     * Builds AuthResponse.UserResponse from User entity only.
     */
    private AuthResponse.UserResponse buildUserResponseFromUser(User user) {
        AuthResponse.UserResponse.UserResponseBuilder builder = AuthResponse.UserResponse.builder()
                .id(user.getId())
                .tpin(user.getTpin())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name())
                .accountStatus(user.getStatus().name())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified());

        // Add type-specific information
        if (user.getUserType() == User.UserType.INDIVIDUAL) {
            individualTaxpayerRepository.findByUser(user).ifPresent(taxpayer -> {
                builder.firstName(taxpayer.getFirstName())
                       .lastName(taxpayer.getLastName());
            });
        } else if (user.getUserType() == User.UserType.BUSINESS) {
            businessTaxpayerRepository.findByUser(user).ifPresent(taxpayer -> {
                builder.businessName(taxpayer.getBusinessName());
            });
        }

        return builder.build();
    }
}