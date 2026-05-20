package com.vti.bevtilib.dto;

import com.vti.bevtilib.model.PostReport;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportPostRequest {

    @NotNull(message = "Lý do báo cáo không được để trống")
    private PostReport.ReportReason reason;

    private String description;
}
