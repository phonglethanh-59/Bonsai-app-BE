package com.vti.bevtilib.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vti.bevtilib.dto.RevenueReportDTO;
import com.vti.bevtilib.model.Order;
import com.vti.bevtilib.model.OrderStatus;
import com.vti.bevtilib.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final OrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ==================== ORDER EXPORT ====================

    @Transactional(readOnly = true)
    public byte[] exportOrdersToExcel(String status, LocalDate fromDate, LocalDate toDate) throws IOException {
        List<Order> orders = getFilteredOrders(status, fromDate, toDate);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Danh sach don hang");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title
            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SACH DON HANG - BONSAI SHOP");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Date range info
            Row dateRow = sheet.createRow(1);
            String dateInfo = "Tu: " + (fromDate != null ? fromDate.format(DATE_FMT) : "Tat ca")
                    + " - Den: " + (toDate != null ? toDate.format(DATE_FMT) : "Tat ca");
            dateRow.createCell(0).setCellValue(dateInfo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Headers
            String[] headers = {"Ma DH", "Khach hang", "Ngay dat", "Tong tien", "Thanh toan", "Trang thai", "Dia chi", "SDT"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0"));
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);

            // Data rows
            int rowIdx = 4;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);

                Cell c0 = row.createCell(0); c0.setCellValue(order.getId()); c0.setCellStyle(dataStyle);

                String customerName = order.getUser() != null && order.getUser().getUserDetail() != null
                        ? order.getUser().getUserDetail().getFullName()
                        : (order.getUser() != null ? order.getUser().getUsername() : "-");
                Cell c1 = row.createCell(1); c1.setCellValue(customerName); c1.setCellStyle(dataStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(order.getOrderDate() != null ? order.getOrderDate().format(DATETIME_FMT) : "-");
                c2.setCellStyle(dataStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(order.getTotalAmount().doubleValue());
                c3.setCellStyle(moneyStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "COD");
                c4.setCellStyle(dataStyle);

                Cell c5 = row.createCell(5); c5.setCellValue(getStatusLabel(order.getStatus())); c5.setCellStyle(dataStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(order.getShippingAddress()); c6.setCellStyle(dataStyle);
                Cell c7 = row.createCell(7); c7.setCellValue(order.getPhone() != null ? order.getPhone() : "-"); c7.setCellStyle(dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportOrdersToPdf(String status, LocalDate fromDate, LocalDate toDate) throws IOException {
        List<Order> orders = getFilteredOrders(status, fromDate, toDate);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font dataFont = new Font(Font.HELVETICA, 9);

            // Title
            Paragraph title = new Paragraph("DANH SACH DON HANG - BONSAI SHOP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            String dateInfo = "Tu: " + (fromDate != null ? fromDate.format(DATE_FMT) : "Tat ca")
                    + " - Den: " + (toDate != null ? toDate.format(DATE_FMT) : "Tat ca");
            Paragraph dateParagraph = new Paragraph(dateInfo, dataFont);
            dateParagraph.setAlignment(Element.ALIGN_CENTER);
            dateParagraph.setSpacingAfter(15);
            document.add(dateParagraph);

            // Table
            PdfPTable table = new PdfPTable(new float[]{1, 3, 3, 2.5f, 2, 2, 4, 2.5f});
            table.setWidthPercentage(100);

            String[] headers = {"Ma DH", "Khach hang", "Ngay dat", "Tong tien", "Thanh toan", "Trang thai", "Dia chi", "SDT"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(37, 99, 235));
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (Order order : orders) {
                addPdfCell(table, String.valueOf(order.getId()), dataFont);

                String customerName = order.getUser() != null && order.getUser().getUserDetail() != null
                        ? order.getUser().getUserDetail().getFullName()
                        : (order.getUser() != null ? order.getUser().getUsername() : "-");
                addPdfCell(table, customerName, dataFont);
                addPdfCell(table, order.getOrderDate() != null ? order.getOrderDate().format(DATETIME_FMT) : "-", dataFont);
                addPdfCell(table, formatMoney(order.getTotalAmount()), dataFont);
                addPdfCell(table, order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "COD", dataFont);
                addPdfCell(table, getStatusLabel(order.getStatus()), dataFont);
                addPdfCell(table, order.getShippingAddress(), dataFont);
                addPdfCell(table, order.getPhone() != null ? order.getPhone() : "-", dataFont);
            }

            document.add(table);

            Paragraph footer = new Paragraph("Tong so don hang: " + orders.size(), dataFont);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
            return out.toByteArray();
        }
    }

    // ==================== REVENUE EXPORT ====================

    @Transactional(readOnly = true)
    public RevenueReportDTO getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.sumRevenueByDateRange(from, to);
        long totalOrders = orderRepository.countByDateRange(from, to);
        long deliveredOrders = orderRepository.countByStatusAndDateRange(OrderStatus.DELIVERED, from, to);
        long cancelledOrders = orderRepository.countByStatusAndDateRange(OrderStatus.CANCELLED, from, to);
        long pendingOrders = orderRepository.countByStatusAndDateRange(OrderStatus.PENDING, from, to);

        List<Object[]> dailyData = orderRepository.findDailyRevenue(from, to);
        List<RevenueReportDTO.DailyRevenue> dailyRevenues = dailyData.stream()
                .map(row -> RevenueReportDTO.DailyRevenue.builder()
                        .date(row[0].toString())
                        .revenue(toBigDecimal(row[1]))
                        .orderCount(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());

        List<Object[]> topProductData = orderRepository.findTopProductsByRevenue(from, to);
        List<RevenueReportDTO.ProductRevenue> topProducts = topProductData.stream()
                .limit(10)
                .map(row -> RevenueReportDTO.ProductRevenue.builder()
                        .productName((String) row[0])
                        .quantitySold(((Number) row[1]).longValue())
                        .revenue(toBigDecimal(row[2]))
                        .build())
                .collect(Collectors.toList());

        return RevenueReportDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .pendingOrders(pendingOrders)
                .dailyRevenues(dailyRevenues)
                .topProducts(topProducts)
                .build();
    }

    @Transactional(readOnly = true)
    public byte[] exportRevenueToExcel(LocalDate fromDate, LocalDate toDate) throws IOException {
        RevenueReportDTO report = getRevenueReport(fromDate, toDate);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // --- Sheet 1: Tong quan ---
            Sheet summarySheet = workbook.createSheet("Tong quan");

            CellStyle titleStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            moneyStyle.setDataFormat(format.getFormat("#,##0"));

            CellStyle labelStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            labelStyle.setFont(boldFont);

            // Title
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BAO CAO DOANH THU - BONSAI SHOP");
            titleCell.setCellStyle(titleStyle);
            summarySheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            Row dateRow = summarySheet.createRow(1);
            dateRow.createCell(0).setCellValue("Tu: " + fromDate.format(DATE_FMT) + " - Den: " + toDate.format(DATE_FMT));

            // Summary stats
            int row = 3;
            Row r = summarySheet.createRow(row++);
            r.createCell(0).setCellValue("Tong doanh thu:");
            Cell revCell = r.createCell(1);
            revCell.setCellValue(report.getTotalRevenue().doubleValue());
            revCell.setCellStyle(moneyStyle);

            r = summarySheet.createRow(row++);
            r.createCell(0).setCellValue("Tong so don hang:");
            r.createCell(1).setCellValue(report.getTotalOrders());

            r = summarySheet.createRow(row++);
            r.createCell(0).setCellValue("Don da giao:");
            r.createCell(1).setCellValue(report.getDeliveredOrders());

            r = summarySheet.createRow(row++);
            r.createCell(0).setCellValue("Don da huy:");
            r.createCell(1).setCellValue(report.getCancelledOrders());

            r = summarySheet.createRow(row++);
            r.createCell(0).setCellValue("Don cho xu ly:");
            r.createCell(1).setCellValue(report.getPendingOrders());

            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);

            // --- Sheet 2: Doanh thu theo ngay ---
            Sheet dailySheet = workbook.createSheet("Doanh thu theo ngay");
            Row dailyTitle = dailySheet.createRow(0);
            dailyTitle.createCell(0).setCellValue("DOANH THU THEO NGAY");

            Row dailyHeader = dailySheet.createRow(2);
            String[] dailyHeaders = {"Ngay", "Doanh thu", "So don hang"};
            for (int i = 0; i < dailyHeaders.length; i++) {
                Cell cell = dailyHeader.createCell(i);
                cell.setCellValue(dailyHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int dailyRow = 3;
            for (RevenueReportDTO.DailyRevenue daily : report.getDailyRevenues()) {
                Row dr = dailySheet.createRow(dailyRow++);
                Cell dc0 = dr.createCell(0); dc0.setCellValue(daily.getDate()); dc0.setCellStyle(dataStyle);
                Cell dc1 = dr.createCell(1); dc1.setCellValue(daily.getRevenue().doubleValue()); dc1.setCellStyle(moneyStyle);
                Cell dc2 = dr.createCell(2); dc2.setCellValue(daily.getOrderCount()); dc2.setCellStyle(dataStyle);
            }

            for (int i = 0; i < dailyHeaders.length; i++) dailySheet.autoSizeColumn(i);

            // --- Sheet 3: Top san pham ---
            Sheet productSheet = workbook.createSheet("Top san pham");
            Row prodTitle = productSheet.createRow(0);
            prodTitle.createCell(0).setCellValue("TOP SAN PHAM BAN CHAY");

            Row prodHeader = productSheet.createRow(2);
            String[] prodHeaders = {"San pham", "So luong ban", "Doanh thu"};
            for (int i = 0; i < prodHeaders.length; i++) {
                Cell cell = prodHeader.createCell(i);
                cell.setCellValue(prodHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int prodRow = 3;
            for (RevenueReportDTO.ProductRevenue product : report.getTopProducts()) {
                Row pr = productSheet.createRow(prodRow++);
                Cell pc0 = pr.createCell(0); pc0.setCellValue(product.getProductName()); pc0.setCellStyle(dataStyle);
                Cell pc1 = pr.createCell(1); pc1.setCellValue(product.getQuantitySold()); pc1.setCellStyle(dataStyle);
                Cell pc2 = pr.createCell(2); pc2.setCellValue(product.getRevenue().doubleValue()); pc2.setCellStyle(moneyStyle);
            }

            for (int i = 0; i < prodHeaders.length; i++) productSheet.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional(readOnly = true)
    public byte[] exportRevenueToPdf(LocalDate fromDate, LocalDate toDate) throws IOException {
        RevenueReportDTO report = getRevenueReport(fromDate, toDate);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(37, 99, 235));
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font dataFont = new Font(Font.HELVETICA, 10);
            Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

            // Title
            Paragraph title = new Paragraph("BAO CAO DOANH THU", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Paragraph subtitle = new Paragraph("BONSAI SHOP", sectionFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(5);
            document.add(subtitle);

            Paragraph dateRange = new Paragraph("Tu " + fromDate.format(DATE_FMT) + " den " + toDate.format(DATE_FMT), dataFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            dateRange.setSpacingAfter(20);
            document.add(dateRange);

            // Summary
            Paragraph summaryTitle = new Paragraph("TONG QUAN", sectionFont);
            summaryTitle.setSpacingAfter(10);
            document.add(summaryTitle);

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            addSummaryRow(summaryTable, "Tong doanh thu:", formatMoney(report.getTotalRevenue()), boldFont, dataFont);
            addSummaryRow(summaryTable, "Tong so don hang:", String.valueOf(report.getTotalOrders()), boldFont, dataFont);
            addSummaryRow(summaryTable, "Don da giao:", String.valueOf(report.getDeliveredOrders()), boldFont, dataFont);
            addSummaryRow(summaryTable, "Don da huy:", String.valueOf(report.getCancelledOrders()), boldFont, dataFont);
            addSummaryRow(summaryTable, "Don cho xu ly:", String.valueOf(report.getPendingOrders()), boldFont, dataFont);

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // Top products
            if (!report.getTopProducts().isEmpty()) {
                Paragraph prodTitle2 = new Paragraph("TOP SAN PHAM BAN CHAY", sectionFont);
                prodTitle2.setSpacingAfter(10);
                document.add(prodTitle2);

                PdfPTable prodTable = new PdfPTable(new float[]{4, 2, 3});
                prodTable.setWidthPercentage(100);

                String[] prodHeaders = {"San pham", "SL ban", "Doanh thu"};
                for (String h : prodHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(37, 99, 235));
                    cell.setPadding(6);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    prodTable.addCell(cell);
                }

                for (RevenueReportDTO.ProductRevenue p : report.getTopProducts()) {
                    addPdfCell(prodTable, p.getProductName(), dataFont);
                    addPdfCell(prodTable, String.valueOf(p.getQuantitySold()), dataFont);
                    addPdfCell(prodTable, formatMoney(p.getRevenue()), dataFont);
                }

                document.add(prodTable);
            }

            document.close();
            return out.toByteArray();
        }
    }

    // ==================== HELPERS ====================

    private List<Order> getFilteredOrders(String status, LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null) {
            LocalDateTime from = fromDate.atStartOfDay();
            LocalDateTime to = toDate.atTime(LocalTime.MAX);
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                return orderRepository.findAllByStatusAndDateRange(orderStatus, from, to);
            }
            return orderRepository.findAllByDateRange(from, to);
        }
        return orderRepository.findAllWithItemsAndUser();
    }

    private String getStatusLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Cho xu ly";
            case CONFIRMED -> "Da xac nhan";
            case SHIPPING -> "Dang giao";
            case DELIVERED -> "Da giao";
            case CANCELLED -> "Da huy";
        };
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f VND", amount);
    }

    private void addPdfCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        return new BigDecimal(value.toString());
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(0);
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(0);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }
}
