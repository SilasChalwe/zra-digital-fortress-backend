package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zm.zra.zra_digital_fortress_backend.dto.response.UserResponse;
import zm.zra.zra_digital_fortress_backend.exception.ResourceNotFoundException;
import zm.zra.zra_digital_fortress_backend.model.*;
import zm.zra.zra_digital_fortress_backend.repository.*;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final IndividualTaxpayerRepository individualTaxpayerRepository;
    private final BusinessTaxpayerRepository businessTaxpayerRepository;
    private final AuditLogService auditLogService;

    public UserResponse getUserProfile(String userId) {
        log.info("Getting user profile for: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .tpin(user.getTpin())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType().name())
                .accountStatus(user.getStatus().name())
                .mfaEnabled(user.getMfaEnabled())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();

        // Add type-specific information
        if (user.getUserType() == User.UserType.INDIVIDUAL) {
            Optional<IndividualTaxpayer> individual = individualTaxpayerRepository.findByUser(user);
            individual.ifPresent(ind -> {
                response.setFirstName(ind.getFirstName());
                response.setLastName(ind.getLastName());
                response.setMiddleName(ind.getMiddleName());
                response.setDateOfBirth(ind.getDateOfBirth());
                response.setNrcNumber(ind.getNrcNumber());
                response.setPhysicalAddress(ind.getPhysicalAddress());
                response.setGender(ind.getGender() != null ? ind.getGender().name() : null);
                response.setEmploymentStatus(ind.getEmploymentStatus() != null ? ind.getEmploymentStatus().name() : null);
            });
        } else if (user.getUserType() == User.UserType.BUSINESS) {
            Optional<BusinessTaxpayer> business = businessTaxpayerRepository.findByUser(user);
            business.ifPresent(biz -> {
                response.setBusinessName(biz.getBusinessName());
                response.setBusinessType(biz.getBusinessType() != null ? biz.getBusinessType().name() : null);
                response.setRegistrationNumber(biz.getRegistrationNumber());
                response.setDateOfIncorporation(biz.getDateOfIncorporation());
                response.setBusinessAddress(biz.getBusinessAddress());
                response.setNumberOfEmployees(biz.getNumberOfEmployees());
                response.setSector(biz.getSector() != null ? biz.getSector().name() : null);
                response.setVatRegistered(biz.getVatRegistered());
            });
        }

        // Add compliance score
        if (user.getComplianceScore() != null) {
            response.setComplianceScore(user.getComplianceScore().getOverallScore());
        }

        return response;
    }

    @Transactional
    public UserResponse updateUserProfile(String userId, UserResponse updateRequest) {
        log.info("Updating user profile for: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update basic user info
        if (updateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        // Update type-specific information
        if (user.getUserType() == User.UserType.INDIVIDUAL) {
            IndividualTaxpayer individual = individualTaxpayerRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Individual taxpayer profile not found"));

            if (updateRequest.getPhysicalAddress() != null) {
                individual.setPhysicalAddress(updateRequest.getPhysicalAddress());
            }
            if (updateRequest.getEmploymentStatus() != null) {
                individual.setEmploymentStatus(IndividualTaxpayer.EmploymentStatus.valueOf(updateRequest.getEmploymentStatus()));
            }

            individualTaxpayerRepository.save(individual);
        } else if (user.getUserType() == User.UserType.BUSINESS) {
            BusinessTaxpayer business = businessTaxpayerRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Business taxpayer profile not found"));

            if (updateRequest.getBusinessAddress() != null) {
                business.setBusinessAddress(updateRequest.getBusinessAddress());
            }
            if (updateRequest.getNumberOfEmployees() != null) {
                business.setNumberOfEmployees(updateRequest.getNumberOfEmployees());
            }

            businessTaxpayerRepository.save(business);
        }

        userRepository.save(user);

        auditLogService.logAction(userId, "USER_PROFILE_UPDATED",
                AuditLog.EntityType.USER, userId, "Profile updated");

        return getUserProfile(userId);
    }

    @Transactional
    public void enableMfa(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setMfaEnabled(true);
        userRepository.save(user);

        auditLogService.logAction(userId, "MFA_ENABLED",
                AuditLog.EntityType.USER, userId, "MFA enabled");

        log.info("MFA enabled for user: {}", userId);
    }

    @Transactional
    public void disableMfa(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);

        auditLogService.logAction(userId, "MFA_DISABLED",
                AuditLog.EntityType.USER, userId, "MFA disabled");

        log.info("MFA disabled for user: {}", userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User getUserByTpin(String tpin) {
        return userRepository.findByTpin(tpin)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with TPIN: " + tpin));
    }
}