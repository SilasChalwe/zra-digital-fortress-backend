package zm.zra.zra_digital_fortress_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zm.zra.zra_digital_fortress_backend.exception.BadRequestException;
import zm.zra.zra_digital_fortress_backend.exception.ResourceNotFoundException;
import zm.zra.zra_digital_fortress_backend.model.AuditLog;
import zm.zra.zra_digital_fortress_backend.model.Document;
import zm.zra.zra_digital_fortress_backend.repository.DocumentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final AuditLogService auditLogService;

    private static final String UPLOAD_DIR = "uploads/documents/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Transactional
    public Document uploadDocument(String userId, MultipartFile file, Document.DocumentType documentType) {
        log.info("Uploading document for user: {}", userId);

        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size of 10MB");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Calculate checksum
            String checksum = calculateChecksum(file.getBytes());

            // Create document record
            Document document = Document.builder()
                    .userId(userId)
                    .documentType(documentType)
                    .fileName(originalFilename)
                    .fileUrl(filePath.toString())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .checksum(checksum)
                    .build();

            document = documentRepository.save(document);

            auditLogService.logAction(userId, "DOCUMENT_UPLOADED",
                    AuditLog.EntityType.SYSTEM, document.getId(),
                    "Document uploaded: " + documentType);

            log.info("Document uploaded successfully: {}", document.getId());

            return document;

        } catch (IOException e) {
            log.error("Error uploading document", e);
            throw new BadRequestException("Error uploading document: " + e.getMessage());
        }
    }

    public Document getDocument(String documentId, String userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        if (!document.getUserId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }

        return document;
    }

    @Transactional
    public void deleteDocument(String documentId, String userId) {
        Document document = getDocument(documentId, userId);

        try {
            // Delete physical file
            Path filePath = Paths.get(document.getFileUrl());
            Files.deleteIfExists(filePath);

            // Delete database record
            documentRepository.delete(document);

            auditLogService.logAction(userId, "DOCUMENT_DELETED",
                    AuditLog.EntityType.SYSTEM, documentId, "Document deleted");

            log.info("Document deleted: {}", documentId);

        } catch (IOException e) {
            log.error("Error deleting document file", e);
            throw new BadRequestException("Error deleting document: " + e.getMessage());
        }
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error calculating checksum", e);
            return "";
        }
    }
}