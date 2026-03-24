package com.vti.bevtilib.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private int rating;
    private String comment;
    private String reviewerName;
    private String reviewDate;
}