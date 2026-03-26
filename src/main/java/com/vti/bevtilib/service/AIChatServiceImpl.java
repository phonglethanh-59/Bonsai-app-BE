package com.vti.bevtilib.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatServiceImpl implements AIChatService {

    private final WebClient geminiWebClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
            Bạn là chuyên gia tư vấn cây cảnh và bonsai với nhiều năm kinh nghiệm. Bạn giúp người dùng:
            1. Đánh giá tình trạng sức khỏe của cây qua mô tả hoặc hình ảnh
            2. Hướng dẫn cách chăm sóc, tưới nước, bón phân, cắt tỉa cho từng loại cây
            3. Chẩn đoán bệnh cây và đề xuất cách phòng/chữa trị
            4. Tư vấn loại cây phù hợp với điều kiện thời tiết, khí hậu từng vùng miền Việt Nam
            5. Chia sẻ kiến thức về kỹ thuật trồng và tạo dáng bonsai

            Quy tắc trả lời:
            - Luôn trả lời bằng tiếng Việt
            - Ngắn gọn, dễ hiểu, thực tế
            - Nếu phân tích ảnh, mô tả chi tiết những gì thấy được và đưa ra đánh giá
            - Nếu được hỏi về thời tiết/vùng miền, tư vấn dựa trên kiến thức khí hậu Việt Nam
            - Sử dụng emoji phù hợp để câu trả lời sinh động hơn
            """;

    @Override
    public String chat(String message, String imageBase64, String location) {
        try {
            String model = "gemini-1.5-flash";

            String fullMessage = buildMessage(message, location);

            Map<String, Object> requestBody = buildRequestBody(fullMessage, imageBase64);

            log.info("Gọi Gemini API với model: {}", model);

            Map response = geminiWebClient
                    .post()
                    .uri("/{model}:generateContent?key={key}", model, apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        return clientResponse.bodyToMono(String.class)
                                .map(body -> {
                                    log.error("Gemini API error: status={}, body={}", clientResponse.statusCode(), body);
                                    return new RuntimeException("Gemini API error: " + body);
                                });
                    })
                    .bodyToMono(Map.class)
                    .block();

            log.info("Gemini API response received");
            return extractReply(response);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API: {}", e.getMessage(), e);
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
        List<Map<String, Object>> parts = new ArrayList<>();

        // System instruction
        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));

        // User message text
        parts.add(Map.of("text", message));

        // Image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String cleanBase64 = imageBase64;
            String mimeType = "image/jpeg";

            if (imageBase64.contains(",")) {
                String[] split = imageBase64.split(",");
                cleanBase64 = split[1];
                if (split[0].contains("png")) mimeType = "image/png";
                else if (split[0].contains("webp")) mimeType = "image/webp";
                else if (split[0].contains("gif")) mimeType = "image/gif";
            }

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", cleanBase64);
            parts.add(Map.of("inlineData", inlineData));
        }

        Map<String, Object> content = new HashMap<>();
        content.put("parts", parts);
        content.put("role", "user");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));
        requestBody.put("systemInstruction", systemInstruction);

        // Safety settings
        List<Map<String, String>> safetySettings = List.of(
                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
        );
        requestBody.put("safetySettings", safetySettings);

        return requestBody;
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map response) {
        if (response == null) return "Không nhận được phản hồi từ AI.";

        try {
            List<Map> candidates = (List<Map>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "AI không thể trả lời câu hỏi này.";

            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Lỗi parse response từ Gemini: ", e);
            return "Có lỗi xảy ra khi xử lý phản hồi từ AI.";
        }
    }
}
