package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.ActionPdf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActionPdfRepository extends JpaRepository<ActionPdf, Long> {
    List<ActionPdf> findAllByOrderByAddedPdfAtDesc();
    Optional<ActionPdf> findByActionPdfName(String actionPdfName);
}
