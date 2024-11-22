package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.PerformancePdf;
import com.MOF_ITMD.DepWeb.repository.PerformancePdfRepository;
import jakarta.servlet.MultipartConfigElement;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PerformancePdfService {
    private final PerformancePdfRepository performancePdfRepository;
    private final MultipartConfigElement multipartConfigElement;

    @Autowired
    public PerformancePdfService(PerformancePdfRepository performancePdfRepository, MultipartConfigElement multipartConfigElement) {
        this.performancePdfRepository = performancePdfRepository;
        this.multipartConfigElement = multipartConfigElement;
    }

    public PerformancePdf savePerformance(MultipartFile file) throws IOException {
        if (!isValidPdfFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Only Pdf files are allowed!");
        }
        String uploadDr = multipartConfigElement.getLocation();
        String performancePdfName = file.getOriginalFilename();

        Optional<PerformancePdf> existingFile = performancePdfRepository.findByPerformancePdfName(performancePdfName);
        if (existingFile.isPresent()) {
            throw new IllegalArgumentException("This file already exists!");
        }

        String subFolder = "Performance Report Pdf";
        String performancePdfPath = uploadDr + subFolder + File.separator + performancePdfName;

        File directory = new File(uploadDr, subFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File pdfFile = new File(performancePdfPath);
        if (pdfFile.exists()) {
            if (!pdfFile.delete()) {
                throw new IOException("Failed to delete the existing file: " + performancePdfPath);
            }
        }
        file.transferTo(pdfFile);
        PerformancePdf performancePdf = new PerformancePdf();
        performancePdf.setPerformancePdfName(performancePdfName);
        performancePdf.setPerformancePdfPath(performancePdfPath);

        String performanceImageName = performancePdfName.replace(".pdf", ".png");
        String subImageFolder = "Performance Report Image";
        String performanceImagePath = uploadDr + subFolder + File.separator + subImageFolder + File.separator + performanceImageName;

        File imgDirectory = new File(uploadDr + subFolder + File.separator, subImageFolder);
        if (!imgDirectory.exists()) {
            imgDirectory.mkdirs();
        }

        try (PDDocument document = Loader.loadPDF(new File(performancePdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            File imageFile = new File(performanceImagePath);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    throw new IOException("Failed to delete the existing file: " + performanceImagePath);
                }
            }
            ImageIO.write(bim, "png", new File(performanceImagePath));
        } catch (IOException e) {
            throw new IOException("Error processing PDF file", e);
        }

        performancePdf.setPerformanceImageName(performanceImageName);
        performancePdf.setPerformanceImagePath(performanceImagePath);
        performancePdf.setAddedPdfAt(LocalDateTime.now());
        performancePdf.setLatestUpdateAt(LocalDateTime.now());

        performancePdfRepository.save(performancePdf);

        return performancePdf;
    }

    private boolean isValidPdfFile(MultipartFile file) {
        return file.getContentType().equalsIgnoreCase("application/pdf");
    }

    public List<PerformancePdf> getAllPerformanceFile() {
        return performancePdfRepository.findAllByOrderByAddedPdfAtDesc();
    }

    public PerformancePdf findPerformanceById(Long id) {
        return performancePdfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance Pdf not found with id: " + id));
    }

    public void deletePerformanceFile(Long id) {
        try {
            Optional<PerformancePdf> optionalPerformanceFile = performancePdfRepository.findById(id);
            if (optionalPerformanceFile.isPresent()) {
                PerformancePdf performancePdf = optionalPerformanceFile.get();
                String pdfPath = performancePdf.getPerformancePdfPath();
                String imagePath = performancePdf.getPerformanceImagePath();
                File pdfFile = new File(pdfPath);
                File imageFile = new File(imagePath);
                if (pdfFile.exists()) {
                    pdfFile.delete();
                }
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                performancePdfRepository.deleteById(id);

            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting performance file with id: " + id, e);
        }

    }

    public PerformancePdf updatePerformanceStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        PerformancePdf tempPerformance = findPerformanceById(id);
        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempPerformance.setPerformanceStatus(updateStatusDTO.getStatus());
        tempPerformance.setLatestUpdateAt(updateStatusDTO.getUpdateAt());

        return performancePdfRepository.save(tempPerformance);
    }
}
