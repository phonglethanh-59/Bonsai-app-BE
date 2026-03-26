package com.vti.bevtilib.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String imageBase64;
    private String location;
}
