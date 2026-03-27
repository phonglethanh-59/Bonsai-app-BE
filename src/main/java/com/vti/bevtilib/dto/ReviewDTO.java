package com.vti.bevtilib.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private String reviewerName;
    private String userId;
    private String reviewDate;
}