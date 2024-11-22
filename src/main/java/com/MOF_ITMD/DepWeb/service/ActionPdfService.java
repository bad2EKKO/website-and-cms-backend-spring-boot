package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.ActionPdf;
import com.MOF_ITMD.DepWeb.repository.ActionPdfRepository;
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
public class ActionPdfService {
    private final ActionPdfRepository actionPdfRepository;
    private final MultipartConfigElement multipartConfigElement;

    @Autowired
    public ActionPdfService(ActionPdfRepository actionPdfRepository, MultipartConfigElement multipartConfigElement) {
        this.actionPdfRepository = actionPdfRepository;
        this.multipartConfigElement = multipartConfigElement;
    }

    public ActionPdf saveAction(MultipartFile file) throws IOException {
        if (!isValidPdfFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Only Pdf files are allowed!");
        }
        String uploadDr = multipartConfigElement.getLocation();
        String actionPdfName = file.getOriginalFilename();

        Optional<ActionPdf> existingFile = actionPdfRepository.findByActionPdfName(actionPdfName);
        if (existingFile.isPresent()) {
            throw new IllegalArgumentException("This file already exists!");
        }

        String subFolder = "Action Plan Pdf";
        String actionPdfPath = uploadDr + subFolder + File.separator + actionPdfName;

        File directory = new File(uploadDr, subFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File pdfFile = new File(actionPdfPath);
        if (pdfFile.exists()) {
            if (!pdfFile.delete()) {
                throw new IOException("Failed to delete the existing file: " + actionPdfPath);
            }
        }
        file.transferTo(pdfFile);
        ActionPdf actionPdf = new ActionPdf();
        actionPdf.setActionPdfName(actionPdfName);
        actionPdf.setActionPdfPath(actionPdfPath);

        String actionImageName = actionPdfName.replace(".pdf", ".png");
        String subImageFolder = "Action Plan Image";
        String actionImagePath = uploadDr + subFolder + File.separator + subImageFolder + File.separator + actionImageName;

        File imgDirectory = new File(uploadDr + subFolder + File.separator, subImageFolder);
        if (!imgDirectory.exists()) {
            imgDirectory.mkdirs();
        }

        try (PDDocument document = Loader.loadPDF(new File(actionPdfPath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            File imageFile = new File(actionImagePath);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    throw new IOException("Failed to delete the existing file: " + actionImagePath);
                }
            }
            ImageIO.write(bim, "png", imageFile);
        } catch (IOException e) {
            throw new IOException("Error processing PDF file", e);
        }

        actionPdf.setActionImageName(actionImageName);
        actionPdf.setActionImagePath(actionImagePath);
        actionPdf.setAddedPdfAt(LocalDateTime.now());
        actionPdf.setLatestStatusUpdateAt(LocalDateTime.now());
        actionPdfRepository.save(actionPdf);

        return actionPdf;
    }

    private boolean isValidPdfFile(MultipartFile file) {
        return file.getContentType().equalsIgnoreCase("application/pdf");
    }

    public List<ActionPdf> getAllActionFile() {
        return actionPdfRepository.findAllByOrderByAddedPdfAtDesc();
    }

    public ActionPdf findActionById(Long id) {
        return actionPdfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action Pdf not found with id: " + id));
    }

    public void deleteActionFile(Long id) {
        try {
            Optional<ActionPdf> optionalActionFile = actionPdfRepository.findById(id);
            if (optionalActionFile.isPresent()) {
                ActionPdf actionPdf = optionalActionFile.get();
                String pdfPath = actionPdf.getActionPdfPath();
                String imagePath = actionPdf.getActionImagePath();
                File pdfFile = new File(pdfPath);
                File imageFile = new File(imagePath);
                if (pdfFile.exists()) {
                    pdfFile.delete();
                }
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                actionPdfRepository.deleteById(id);

            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting action file with id: " + id, e);
        }

    }


    public ActionPdf updateActionStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        ActionPdf tempAction = findActionById(id);
        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempAction.setActionStatus(updateStatusDTO.getStatus());
        tempAction.setLatestStatusUpdateAt(updateStatusDTO.getUpdateAt());

        return actionPdfRepository.save(tempAction);
    }
}
