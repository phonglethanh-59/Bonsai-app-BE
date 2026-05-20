package com.vti.bevtilib.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    @NotBlank(message = "Nội dung bình luận không được để trống")
    @Size(max = 1000, message = "Bình luận tối đa 1000 ký tự")
    private String content;

    private Long parentCommentId; // null = comment gốc, có giá trị = reply
}
