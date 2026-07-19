package com.example.report.service;

import com.example.report.entity.ReportType;
import com.example.report.util.FileStorageService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

/**
 * Generates the actual report file (CSV / Excel / PDF) from request parameters,
 * then delegates persistence to the configured FileStorageService (local disk or S3).
 *
 * NOTE: content here is illustrative — in a real system this would pull rows from
 * a reporting datastore / data warehouse based on `parameters` instead of writing
 * the parameters map itself.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileGenerationService {

    private final FileStorageService fileStorageService;

    private static final Path TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir"), "report-generation");

    public record GeneratedFile(String filePath, String downloadUrl) {}

    public GeneratedFile generate(UUID reportId, ReportType reportType, Map<String, Object> parameters)
            throws IOException {
        Files.createDirectories(TEMP_DIR);

        Path localFile = switch (reportType) {
            case CSV -> generateCsv(reportId, parameters);
            case EXCEL -> generateExcel(reportId, parameters);
            case PDF -> generatePdf(reportId, parameters);
        };

        String extension = switch (reportType) {
            case CSV -> "csv";
            case EXCEL -> "xlsx";
            case PDF -> "pdf";
        };
        String objectKey = "reports/" + reportId + "." + extension;

        String downloadUrl = fileStorageService.store(localFile, objectKey);

        Files.deleteIfExists(localFile);

        return new GeneratedFile(objectKey, downloadUrl);
    }

    private Path generateCsv(UUID reportId, Map<String, Object> parameters) throws IOException {
        Path file = TEMP_DIR.resolve(reportId + ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(file.toFile()))) {
            writer.writeNext(new String[]{"Key", "Value"});
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    writer.writeNext(new String[]{entry.getKey(), String.valueOf(entry.getValue())});
                }
            }
            writer.writeNext(new String[]{"generatedFor", reportId.toString()});
        }
        log.debug("Generated CSV report at {}", file);
        return file;
    }

    private Path generateExcel(UUID reportId, Map<String, Object> parameters) throws IOException {
        Path file = TEMP_DIR.resolve(reportId + ".xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Key");
            header.createCell(1).setCellValue("Value");

            int rowIdx = 1;
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.createCell(1).setCellValue(String.valueOf(entry.getValue()));
                }
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (var out = Files.newOutputStream(file)) {
                workbook.write(out);
            }
        }
        log.debug("Generated Excel report at {}", file);
        return file;
    }

    private Path generatePdf(UUID reportId, Map<String, Object> parameters) throws IOException {
        Path file = TEMP_DIR.resolve(reportId + ".pdf");
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                content.newLineAtOffset(50, 750);
                content.showText("Report: " + reportId);
                content.endText();

                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                int y = 720;
                if (parameters != null) {
                    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                        content.beginText();
                        content.newLineAtOffset(50, y);
                        content.showText(entry.getKey() + ": " + entry.getValue());
                        content.endText();
                        y -= 18;
                    }
                }
            }

            document.save(file.toFile());
        }
        log.debug("Generated PDF report at {}", file);
        return file;
    }
}
