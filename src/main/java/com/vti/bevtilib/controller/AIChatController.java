package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.ChatRequest;
import com.vti.bevtilib.dto.ChatResponse;
import com.vti.bevtilib.service.AIChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply = aiChatService.chat(
                request.getMessage(),
                request.getImageBase64(),
                request.getLocation()
        );
        return ResponseEntity.ok(new ChatResponse(reply, LocalDateTime.now()));
    }

    @PostMapping("/image")
    public ResponseEntity<ChatResponse> chatWithImage(
            @RequestParam("message") String message,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "location", required = false) String location
    ) {
        try {
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());
            String mimeType = image.getContentType() != null ? image.getContentType() : "image/jpeg";
            String imageData = "data:" + mimeType + ";base64," + base64;

            String reply = aiChatService.chat(message, imageData, location);
            return ResponseEntity.ok(new ChatResponse(reply, LocalDateTime.now()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Lỗi xử lý ảnh: " + e.getMessage(), LocalDateTime.now()));
        }
    }
}
