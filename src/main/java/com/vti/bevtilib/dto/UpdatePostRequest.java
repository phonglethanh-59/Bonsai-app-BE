package com.vti.bevtilib.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdatePostRequest {

    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @Size(max = 5000, message = "Nội dung tối đa 5000 ký tự")
    private String content;

    private List<String> tags;

    private List<String> imageUrls;
}
