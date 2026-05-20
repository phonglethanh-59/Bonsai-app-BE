package com.vti.bevtilib.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreatePostRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 5000, message = "Nội dung tối đa 5000 ký tự")
    private String content;

    private List<String> tags = new ArrayList<>();

    private List<String> imageUrls = new ArrayList<>();
}
