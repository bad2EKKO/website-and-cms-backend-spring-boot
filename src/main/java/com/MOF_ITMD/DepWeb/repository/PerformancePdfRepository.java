package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.PerformancePdf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformancePdfRepository extends JpaRepository<PerformancePdf, Long> {
    List<PerformancePdf> findAllByOrderByAddedPdfAtDesc();

    Optional<PerformancePdf> findByPerformancePdfName(String performancePdfName);
}
