package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.HardwareSpecification;
import com.MOF_ITMD.DepWeb.models.SpecifyDoc;
import com.MOF_ITMD.DepWeb.repository.HardwareSpecificationRepository;
import com.MOF_ITMD.DepWeb.repository.SpecifyDocRepository;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HardwareSpecificationService {
    private final HardwareSpecificationRepository hardwareSpecificationRepository;
    private final SpecifyDocRepository specifyDocRepository;
    private final MultipartConfigElement multipartConfigElement;

    @Autowired
    public HardwareSpecificationService(HardwareSpecificationRepository hardwareSpecificationRepository, SpecifyDocRepository specifyDocRepository, MultipartConfigElement multipartConfigElement) {
        this.hardwareSpecificationRepository = hardwareSpecificationRepository;
        this.specifyDocRepository = specifyDocRepository;
        this.multipartConfigElement = multipartConfigElement;
    }

    public HardwareSpecification saveSpecification(HardwareSpecification hardwareSpecification) {
        hardwareSpecification.setSpeCreatedAt(LocalDateTime.now());
        hardwareSpecification.setLatestUpdateAt(LocalDateTime.now());
        return hardwareSpecificationRepository.save(hardwareSpecification);
    }

    public List<SpecifyDoc> saveSpecifyDocToSpecification(Long spcId, MultipartFile[] docs) throws IOException {
        HardwareSpecification tempSpecification = getSpecificationById(spcId);
        tempSpecification.setLatestUpdateAt(LocalDateTime.now());
        List<SpecifyDoc> docsList = new ArrayList<>();
        for (MultipartFile doc : docs) {
            if (!isValidDoc(doc)) {
                System.out.println("Invalid doc format: " + doc.getOriginalFilename());
                continue;
            }
            String specificationFolder = tempSpecification.getSpecificationType();
            String docName = doc.getOriginalFilename();
            String docType = doc.getContentType();
            String motherFolder = "Hardware Specification";
            String uploadDr = multipartConfigElement.getLocation() + motherFolder;
            String docPath = uploadDr + File.separator + specificationFolder + File.separator + docName;

            File directory = new File(uploadDr, specificationFolder);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File docFile = new File(docPath);
            if (docFile.exists()) {
                if (!docFile.delete()) {
                    throw new IOException("Failed to delete the existing file: " + docName);
                }
            }
            doc.transferTo(docFile);

            SpecifyDoc specifyDoc = new SpecifyDoc();
            specifyDoc.setDocName(docName);
            specifyDoc.setDocType(docType);
            specifyDoc.setDocPath(docPath);
            specifyDoc.setCreatedAt(LocalDateTime.now());
            specifyDoc.setLatestUpdateAt(LocalDateTime.now());
            specifyDoc.setHardwareSpecification(tempSpecification);
            docsList.add(saveSpecifyDoc(specifyDoc));
        }
        return docsList;
    }

    private SpecifyDoc saveSpecifyDoc(SpecifyDoc specifyDoc) {
        return specifyDocRepository.save(specifyDoc);
    }

    private boolean isValidDoc(MultipartFile doc) {
        String docType = doc.getOriginalFilename();
        return docType.endsWith(".pdf") || docType.endsWith(".doc") || docType.endsWith(".docx");
    }

    private HardwareSpecification getSpecificationById(Long spcId) {
        return hardwareSpecificationRepository.findById(spcId)
                .orElseThrow(() -> new RuntimeException("Specification not found with ID: " + spcId));
    }

    public List<HardwareSpecification> getAllHardwareSpecifications() {
        return hardwareSpecificationRepository.findAll();
    }

    public List<SpecifyDoc> getDocBySpecId(Long specId) {
        HardwareSpecification hardwareSpecification = getSpecificationById(specId);
        return hardwareSpecification.getSpecifyDocs();
    }

    public SpecifyDoc getDocByDocId(Long docId) {
        return specifyDocRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
    }

    public void deleteSpecification(Long specId) {
        String parentFolder = "Hardware Specification";
        String eventDr = multipartConfigElement.getLocation() + parentFolder;
        HardwareSpecification tempSpec = getSpecificationById(specId);
        if (!tempSpec.getSpecifyDocs().isEmpty()) {
            try {
                Path folderPath = Paths.get(eventDr, tempSpec.getSpecificationType());
                deleteFolder(folderPath);
            } catch (IOException e) {
                throw new RuntimeException("Error deleting data: " + tempSpec.getSpecificationType(), e);
            }
        }
        hardwareSpecificationRepository.delete(tempSpec);
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

    public void deleteDocFile(Long docId) {
        try {
            HardwareSpecification tempSpecification = getSpecificationByDocId(docId);
            tempSpecification.setLatestUpdateAt(LocalDateTime.now());
            SpecifyDoc tempDoc = getDocByDocId(docId);
            String docPath = tempDoc.getDocPath();
            File docFile = new File(docPath);
            if (docFile.exists()) {
                docFile.delete();
            }
            specifyDocRepository.deleteById(docId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting document file with id: " + docId, e);
        }
    }

    public HardwareSpecification updateSpecificationStatus(Long speId, UpdateStatusDTO updateStatusDTO) {
        HardwareSpecification tempSpecification = getSpecificationById(speId);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempSpecification.setStatus(updateStatusDTO.getStatus());
        tempSpecification.setLatestUpdateAt(updateStatusDTO.getUpdateAt());

        return hardwareSpecificationRepository.save(tempSpecification);
    }

    public SpecifyDoc updateDocStatus(Long docId, UpdateStatusDTO updateStatusDTO) {
        SpecifyDoc tempDoc = getDocByDocId(docId);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());

        HardwareSpecification tempSpecification = getSpecificationByDocId(docId);
        tempSpecification.setLatestUpdateAt(updateStatusDTO.getUpdateAt());

        tempDoc.setDocStatus(updateStatusDTO.getStatus());
        tempDoc.setLatestUpdateAt(updateStatusDTO.getUpdateAt());

        return specifyDocRepository.save(tempDoc);
    }

    public HardwareSpecification getSpecificationByDocId(Long docId) {
        return specifyDocRepository.findSpecificationByDocId(docId);
    }
}
