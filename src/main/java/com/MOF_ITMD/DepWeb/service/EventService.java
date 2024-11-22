package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.Event;
import com.MOF_ITMD.DepWeb.models.Media;
import com.MOF_ITMD.DepWeb.repository.EventRepository;
import com.MOF_ITMD.DepWeb.repository.MediaRepository;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;

    private final MediaRepository mediaRepository;
    private final MultipartConfigElement multipartConfigElement;

    @Autowired
    public EventService(EventRepository eventRepository, MediaRepository mediaRepository, MultipartConfigElement multipartConfigElement) {
        this.eventRepository = eventRepository;
        this.mediaRepository = mediaRepository;
        this.multipartConfigElement = multipartConfigElement;
    }

    public Event saveEvent(Event event) {
        event.setAddedEventAt(LocalDateTime.now());
        event.setLatestUpdateAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Media saveMedia(Media media) {
        return mediaRepository.save(media);
    }

    public Event getEventByID(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
    }

    public List<Event> getAllEvent() {
        return eventRepository.findAllByOrderByAddedEventAtDesc();
    }

    public List<Media> getContentByEventId(Long eventId) {
        Event event = getEventByID(eventId);
        return event.getMedia();
    }

    public Optional<Media> findByContentName(String contentName) {
        return mediaRepository.findByContentName(contentName);
    }

    public boolean isValidEventContent(MultipartFile file) {
        String eventType = file.getContentType();
        return eventType.startsWith("image/") || eventType.startsWith("video/");
    }

    public String setContentNameAsUnique(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int extensionIndex = originalFileName.lastIndexOf(".");
        if (extensionIndex > 0) {
            fileExtension = originalFileName.substring(extensionIndex);
        }
        String uniqueContentName = UUID.randomUUID().toString() + fileExtension;
        return uniqueContentName;
    }

    public String getContentPathByName(String contentName) {
        Optional<Media> contentMedia = findByContentName(contentName);
        if (contentMedia.isPresent()) {
            Media media = contentMedia.get();
            return media.getContentPath();
        } else {
            throw new RuntimeException("Content not found for name: " + contentName);
        }
    }

    public void deleteEvent(Long eventId) {
        String eventParentFolder = "Events";
        String eventDr = multipartConfigElement.getLocation() + eventParentFolder;
        Event event = getEventByID(eventId);
        if (event.getMedia().size() != 0) {
            try {
                Path eventPath = Paths.get(eventDr, event.getEventName());
                deleteFolder(eventPath);
            } catch (IOException e) {
                throw new RuntimeException("Error deleting file: " + event.getEventName(), e);
            }
        }
        eventRepository.delete(event);

    }

    private void deleteFolder(Path folderPath) throws IOException {
        if (Files.exists(folderPath)) {
            if (Files.isDirectory(folderPath)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                    for (Path entry : stream) {
                        if (Files.isDirectory(entry)) {
                            deleteFolder(entry);
                        } else {
                            Files.delete(entry);
                        }
                    }
                }
                Files.delete(folderPath);
            }
        } else {
            throw new IllegalArgumentException("Folder does not exist.");
        }
    }

    public Event updateEventStatus(Long eventId, UpdateStatusDTO updateStatusDTO) {
        Event tempEvent = getEventByID(eventId);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempEvent.setStatus(updateStatusDTO.getStatus());
        tempEvent.setLatestUpdateAt(updateStatusDTO.getUpdateAt());

        return eventRepository.save(tempEvent);
    }
}
