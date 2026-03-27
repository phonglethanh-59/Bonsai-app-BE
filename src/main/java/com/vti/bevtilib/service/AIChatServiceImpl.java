package com.vti.bevtilib.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final WebClient aiWebClient;

    private static final String MODEL = "llama-3.3-70b-versatile";

    private static final String SYSTEM_PROMPT = """
            Bạn là chuyên gia tư vấn cây cảnh và bonsai với nhiều năm kinh nghiệm. Bạn giúp người dùng:
            1. Đánh giá tình trạng sức khỏe của cây qua mô tả
            2. Hướng dẫn cách chăm sóc, tưới nước, bón phân, cắt tỉa cho từng loại cây
            3. Chẩn đoán bệnh cây và đề xuất cách phòng/chữa trị
            4. Tư vấn loại cây phù hợp với điều kiện thời tiết, khí hậu từng vùng miền Việt Nam
            5. Chia sẻ kiến thức về kỹ thuật trồng và tạo dáng bonsai

            Quy tắc trả lời:
            - Luôn trả lời bằng tiếng Việt
            - Ngắn gọn, dễ hiểu, thực tế
            - Nếu được hỏi về thời tiết/vùng miền, tư vấn dựa trên kiến thức khí hậu Việt Nam
            - Sử dụng emoji phù hợp để câu trả lời sinh động hơn
            """;

    @Override
    public String chat(String message, String imageBase64, String location) {
        try {
            String fullMessage = buildMessage(message, location);
            Map<String, Object> requestBody = buildRequestBody(fullMessage, imageBase64);

            log.info("Gọi Groq API với model: {}", MODEL);

            Map response = aiWebClient
                    .post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Groq API error: status={}, body={}", clientResponse.statusCode(), body);
                                    return new RuntimeException("Groq API error: " + body);
                                });
                    })
                    .bodyToMono(Map.class)
                    .block();

            log.info("Groq API response received");
            return extractReply(response);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Groq API: {}", e.getMessage(), e);
            return "Xin lỗi, hiện tại hệ thống AI đang gặp sự cố. Vui lòng thử lại sau. Chi tiết: " + e.getMessage();
        }
    }

    private String buildMessage(String message, String location) {
        StringBuilder sb = new StringBuilder();
        if (location != null && !location.isEmpty()) {
            sb.append("Người dùng đang ở khu vực: ").append(location).append(". ");
            sb.append("Hãy tư vấn dựa trên điều kiện khí hậu, thời tiết đặc trưng của vùng này. ");
        }
        sb.append(message);
        return sb.toString();
    }

    private Map<String, Object> buildRequestBody(String message, String imageBase64) {
        List<Map<String, Object>> messages = new ArrayList<>();

        // System message
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        // User message
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            // Vision request with image
            List<Map<String, Object>> contentParts = new ArrayList<>();
            contentParts.add(Map.of("type", "text", "text", message));

            String imageUrl = imageBase64;
            if (!imageBase64.startsWith("data:")) {
                imageUrl = "data:image/jpeg;base64," + imageBase64;
            }

            contentParts.add(Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageUrl)
            ));

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", contentParts);
            messages.add(userMessage);
        } else {
            // Text-only request
            messages.add(Map.of("role", "user", "content", message));
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 2048);
        requestBody.put("temperature", 0.7);
        return requestBody;
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map response) {
        if (response == null) return "Không nhận được phản hồi từ AI.";

        try {
            List<Map> choices = (List<Map>) response.get("choices");
            if (choices == null || choices.isEmpty()) return "AI không thể trả lời câu hỏi này.";

            Map messageMap = (Map) choices.get(0).get("message");
            return (String) messageMap.get("content");
        } catch (Exception e) {
            log.error("Lỗi parse response từ Groq: ", e);
            return "Có lỗi xảy ra khi xử lý phản hồi từ AI.";
        }
    }
}
