package zm.zra.zra_digital_fortress_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import zm.zra.zra_digital_fortress_backend.dto.request.ChatRequest;
import zm.zra.zra_digital_fortress_backend.dto.response.ApiResponse;
import zm.zra.zra_digital_fortress_backend.dto.response.ChatResponse;
import zm.zra.zra_digital_fortress_backend.integration.AiServiceClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Chatbot", description = "AI Chatbot APIs")
public class ChatbotController {

    private final AiServiceClient aiServiceClient;

    @PostMapping("/chat")
    @Operation(summary = "Send message to chatbot")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> aiResponse = aiServiceClient.getChatbotResponse(
                request.getMessage(),
                request.getLanguage() != null ? request.getLanguage() : "EN"
        );

        ChatResponse chatResponse = ChatResponse.builder()
                .conversationId(request.getConversationId() != null ? 
                        request.getConversationId() : UUID.randomUUID().toString())
                .response((String) aiResponse.get("response"))
                .suggestedActions(Arrays.asList((String[]) aiResponse.get("suggestedActions")))
                .escalateToHuman((Boolean) aiResponse.get("escalateToHuman"))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.<ChatResponse>builder()
                .success(true)
                .message("Chatbot response generated")
                .data(chatResponse)
                .build());
    }
}