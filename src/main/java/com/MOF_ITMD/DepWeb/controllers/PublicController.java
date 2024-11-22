package com.MOF_ITMD.DepWeb.controllers;

import com.MOF_ITMD.DepWeb.models.*;
import com.MOF_ITMD.DepWeb.models.users.UserDTO;
import com.MOF_ITMD.DepWeb.security.services.UserDetailsImpl;
import com.MOF_ITMD.DepWeb.service.*;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public")
public class PublicController {


    private final InquiriesService inquiriesService;
    private final NewsService newsService;
    private final ActionPdfService actionPdfService;
    private final PerformancePdfService performancePdfService;
    private final EventService eventService;
    private final HardwareSpecificationService hardwareSpecificationService;
    private final MultipartConfigElement multipartElement;


    @Autowired
    public PublicController(InquiriesService inquiriesService, NewsService newsService, ActionPdfService actionPdfService, PerformancePdfService performancePdfService, EventService eventService, HardwareSpecificationService hardwareSpecificationService, MultipartConfigElement multipartElement) {
        this.inquiriesService = inquiriesService;
        this.newsService = newsService;
        this.actionPdfService = actionPdfService;
        this.performancePdfService = performancePdfService;
        this.eventService = eventService;
        this.hardwareSpecificationService = hardwareSpecificationService;
        this.multipartElement = multipartElement;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<String> submitInq(@RequestBody InquiriesForm inquiriesForm) {
        try {
            inquiriesService.sendInquiries(inquiriesForm);
            inquiriesService.SendReplyEmail(inquiriesForm);

            return ResponseEntity.ok("Form submitted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error");

        }
    }

    //    News public endpoints
    @GetMapping("/news/latest")
    public List<News> getLast5News() {
        return newsService.getLast5ActiveNews();
    }

//   Action plane public endpoints

    @GetMapping("/action-pdfs")
    public List<ActionPdf> getAllActionFiles() {
        return actionPdfService.getAllActionFile();
    }

    @GetMapping("/action-pdf/pdf/{actionPdfName}")
    public ResponseEntity<Resource> serveActionPdfFile(@PathVariable String actionPdfName) {
        try {
            String uploadDr = multipartElement.getLocation();
            Path pdfFile = Paths.get(uploadDr + "Action Plan Pdf").resolve(actionPdfName);
            Resource resource = new UrlResource(pdfFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the Pdf file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the pdf file!", e);
        }
    }

    @GetMapping("/action-pdf/image/{actionImageName}")
    public ResponseEntity<Resource> serveActionImageFile(@PathVariable String actionImageName) {
        try {
            String uploadDr = multipartElement.getLocation();
            Path ImageFile = Paths.get(uploadDr + "Action Plan Pdf" + File.separator + "Action Plan Image").resolve(actionImageName);
            Resource resource = new UrlResource(ImageFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the Image file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the image file!", e);
        }
    }

    // Performance report public endpoints
    @GetMapping("/performance-pdfs")
    public List<PerformancePdf> getAllPerformanceFiles() {
        return performancePdfService.getAllPerformanceFile();
    }

    @GetMapping("/performance-pdf/pdf/{performancePdfName}")
    public ResponseEntity<Resource> servePerformancePdfFile(@PathVariable String performancePdfName) {
        try {
            String uploadDr = multipartElement.getLocation();
            Path pdfFile = Paths.get(uploadDr + "Performance Report Pdf").resolve(performancePdfName);
            Resource resource = new UrlResource(pdfFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the Pdf file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the pdf file!", e);
        }
    }

    @GetMapping("/performance-pdf/image/{performanceImageName}")
    public ResponseEntity<Resource> servePerformanceImageFile(@PathVariable String performanceImageName) {
        try {
            String uploadDr = multipartElement.getLocation();
            Path ImageFile = Paths.get(uploadDr + "Performance Report Pdf" + File.separator + "Performance Report Image").resolve(performanceImageName);
            Resource resource = new UrlResource(ImageFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the Image file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the image file!", e);
        }
    }

//    Events public endpoints

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllEvent() {
        try {
            List<Event> events = eventService.getAllEvent();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/event/content/{eventId}")
    public ResponseEntity<List<Media>> getContentByEventId(@PathVariable Long eventId) {
        try {
            List<Media> contentList = eventService.getContentByEventId(eventId);
            return ResponseEntity.ok(contentList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/event/content/serve/{contentName:.+}")
    public ResponseEntity<Resource> serveContentFile(@PathVariable String contentName) {
        try {
            Path contentPath = Paths.get(eventService.getContentPathByName(contentName)).toAbsolutePath().normalize();
            Resource resource = new UrlResource(contentPath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //    Hardware Specification endpoints
    @GetMapping("/hardware-specifications")
    public ResponseEntity<List<HardwareSpecification>> getAllHardwareSpecification() {
        try {
            List<HardwareSpecification> specifications = hardwareSpecificationService.getAllHardwareSpecifications();
            return ResponseEntity.ok(specifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/hardware-specification/{specId}/specify-docs")
    public ResponseEntity<List<SpecifyDoc>> getDocBySpecId(@PathVariable Long specId) {
        try {
            List<SpecifyDoc> docList = hardwareSpecificationService.getDocBySpecId(specId);
            return ResponseEntity.ok(docList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/hardware-specification/specify-doc/doc/{docId}")
    public ResponseEntity<Resource> serveDocByDocId(@PathVariable Long docId) {
        try {
            SpecifyDoc tempDoc = hardwareSpecificationService.getDocByDocId(docId);
            Path docPath = Paths.get(tempDoc.getDocPath());
            Resource resource = new UrlResource(docPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the DOC file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the DOC file!", e);
        }
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new UserDTO(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities());
    }


    @GetMapping("/hi")
    public String allAccess() {
        return "For public peoples!";
    }

}
