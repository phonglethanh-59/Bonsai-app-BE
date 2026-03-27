package com.vti.bevtilib.controller;

import com.vti.bevtilib.dto.RevenueReportDTO;
import com.vti.bevtilib.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ExportService exportService;

    // ==================== ORDER EXPORT ====================

    @GetMapping("/orders/excel")
    public ResponseEntity<byte[]> exportOrdersExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws IOException {

        byte[] data = exportService.exportOrdersToExcel(status, fromDate, toDate);
        String filename = "don-hang_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/orders/pdf")
    public ResponseEntity<byte[]> exportOrdersPdf(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws IOException {

        byte[] data = exportService.exportOrdersToPdf(status, fromDate, toDate);
        String filename = "don-hang_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    // ==================== REVENUE REPORT ====================

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDTO> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        RevenueReportDTO report = exportService.getRevenueReport(fromDate, toDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/revenue/excel")
    public ResponseEntity<byte[]> exportRevenueExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws IOException {

        byte[] data = exportService.exportRevenueToExcel(fromDate, toDate);
        String filename = "doanh-thu_" + fromDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                + "_" + toDate.format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/revenue/pdf")
    public ResponseEntity<byte[]> exportRevenuePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) throws IOException {

        byte[] data = exportService.exportRevenueToPdf(fromDate, toDate);
        String filename = "doanh-thu_" + fromDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                + "_" + toDate.format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
