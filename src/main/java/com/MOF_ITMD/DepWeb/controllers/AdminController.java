package com.MOF_ITMD.DepWeb.controllers;

import com.MOF_ITMD.DepWeb.dto.UpdateNewsDTO;
import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.*;
import com.MOF_ITMD.DepWeb.service.*;
import jakarta.servlet.MultipartConfigElement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ActionPdfService actionPdfService;
    private final PerformancePdfService performancePdfService;
    private final NewsService newsService;
    private final EventService eventService;
    private final MultipartConfigElement multipartElement;
    private final HardwareSpecificationService hardwareSpecificationService;

    public AdminController(NewsService newsService, ActionPdfService actionPdfService, PerformancePdfService performancePdfService, EventService eventService, MultipartConfigElement multipartElement, HardwareSpecificationService hardwareSpecificationService) {
        this.actionPdfService = actionPdfService;
        this.performancePdfService = performancePdfService;
        this.newsService = newsService;
        this.eventService = eventService;
        this.multipartElement = multipartElement;
        this.hardwareSpecificationService = hardwareSpecificationService;
    }


    //    News admin endpoints
    @PostMapping("/news")
    public ResponseEntity<News> addNews(@Valid @RequestBody News data) {
        News addNewsItem = newsService.addNews(data);
        return new ResponseEntity<>(addNewsItem, HttpStatus.CREATED);
    }

    @GetMapping("/news")
    public List<News> getAllNews() {
        return newsService.getAllNewsOrderedByDate();
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        News getById = newsService.getNewsById(id);
        return new ResponseEntity<>(getById, HttpStatus.OK);
    }

    @PutMapping("/news/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @Valid @RequestBody UpdateNewsDTO updateNewsDTO) {
        News updateById = newsService.updateNews(id, updateNewsDTO);
        return new ResponseEntity<>(updateById, HttpStatus.OK);
    }

    @PutMapping("/news/status/{id}")
    public ResponseEntity<News> updateNewsStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            News updatedNews = newsService.updateNewsStatus(id, updateStatusDTO);
            return ResponseEntity.ok(updatedNews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //    Action file admin endpoints
    @PostMapping("/action-pdf")
    public ResponseEntity<String> uploadActionFile(@RequestParam("file") MultipartFile file) {
        try {
            actionPdfService.saveAction(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PutMapping("/action-pdf/status/{id}")
    public ResponseEntity<ActionPdf> updateActionStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            ActionPdf updatedStatus = actionPdfService.updateActionStatus(id, updateStatusDTO);
            return ResponseEntity.ok(updatedStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/action-pdf/{id}")
    public ResponseEntity<Void> deleteActionFile(@PathVariable Long id) {
        try {
            actionPdfService.deleteActionFile(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    Performance report admin endpoints

    @PostMapping("/performance-pdf")
    public ResponseEntity<String> uploadPerformanceFile(@RequestParam("file") MultipartFile file) {
        try {
            performancePdfService.savePerformance(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PutMapping("/performance-pdf/status/{id}")
    public ResponseEntity<PerformancePdf> updatePerformanceStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            PerformancePdf updatedStatus = performancePdfService.updatePerformanceStatus(id, updateStatusDTO);
            return ResponseEntity.ok(updatedStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/performance-pdf/{id}")
    public ResponseEntity<Void> deletePerformanceFile(@PathVariable Long id) {
        try {
            performancePdfService.deletePerformanceFile(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    Events admin endpoints
    @PostMapping("/event")
    ResponseEntity<Event> createEvent(@RequestBody Event event) {
        try {
            Event createEvent = eventService.saveEvent(event);
            return ResponseEntity.ok(createEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/event/{eventId}/media")
    ResponseEntity<List<Media>> uploadMedia(@PathVariable Long eventId, @RequestParam("files") MultipartFile[] files) {
        try {
            Event event = eventService.getEventByID(eventId);
            event.setLatestUpdateAt(LocalDateTime.now());
            List<Media> contentList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!eventService.isValidEventContent(file)) {
                    System.out.println("Invalid file format: " + file.getOriginalFilename());
                    continue;
                }
                String eventFolder = event.getEventName();
                String contentName = eventService.setContentNameAsUnique(file);
                String eventParentFolder = "Events";
                String uploadDr = multipartElement.getLocation() + eventParentFolder;
                String contentPath = uploadDr + File.separator + eventFolder + File.separator + contentName;
                File directory = new File(uploadDr, eventFolder);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                try {
                    Files.copy(file.getInputStream(), Paths.get(contentPath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }

                Media media = new Media();
                media.setContentName(contentName);
                media.setContentType(file.getContentType());
                media.setContentPath(contentPath);
                media.setEvent(event);
                contentList.add(eventService.saveMedia(media));
            }
            return ResponseEntity.ok(contentList);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/event/status/{eventId}")
    public ResponseEntity<Event> updateEventStatus(@PathVariable Long eventId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            Event updatedStatusEvent = eventService.updateEventStatus(eventId, updateStatusDTO);
            return ResponseEntity.ok(updatedStatusEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //    Hardware Specification endpoints
    @PostMapping("/hardware-specification")
    public ResponseEntity<HardwareSpecification> addSpecification(@RequestBody HardwareSpecification hardwareSpecification) {
        try {
            HardwareSpecification addSpecification = hardwareSpecificationService.saveSpecification(hardwareSpecification);
            return ResponseEntity.ok(addSpecification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/hardware-specification/specify-doc/{speId}")
    public ResponseEntity<List<SpecifyDoc>> saveSpecifyDocToSpecification(@PathVariable Long speId, @RequestParam("docs") MultipartFile[] docs) {
        try {
            List<SpecifyDoc> saveDocs = hardwareSpecificationService.saveSpecifyDocToSpecification(speId, docs);
            return ResponseEntity.ok(saveDocs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/hardware-specification/status/{speId}")
    public ResponseEntity<HardwareSpecification> updateSpecificationStatus(@PathVariable Long speId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            HardwareSpecification updatedSpecification = hardwareSpecificationService.updateSpecificationStatus(speId, updateStatusDTO);
            return ResponseEntity.ok(updatedSpecification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/hardware-specification/specify-doc/status/{docId}")
    public ResponseEntity<SpecifyDoc> updateDocStatus(@PathVariable Long docId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            SpecifyDoc updatedDoc = hardwareSpecificationService.updateDocStatus(docId, updateStatusDTO);
            return ResponseEntity.ok(updatedDoc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/hardware-specification/{specId}")
    public ResponseEntity<Void> deleteSpecification(@PathVariable Long specId) {
        try {
            hardwareSpecificationService.deleteSpecification(specId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/hardware-specification/specify-doc/{docId}")
    public ResponseEntity<Void> deleteDocFile(@PathVariable Long docId) {
        try {
            hardwareSpecificationService.deleteDocFile(docId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
