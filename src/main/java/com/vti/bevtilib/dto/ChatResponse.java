package com.vti.bevtilib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String reply;
    private LocalDateTime timestamp;
}
