package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.HardwareSpecification;
import com.MOF_ITMD.DepWeb.models.SpecifyDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpecifyDocRepository extends JpaRepository<SpecifyDoc, Long> {

    @Query("SELECT s.hardwareSpecification FROM SpecifyDoc s WHERE s.id = :docId")
    HardwareSpecification findSpecificationByDocId(@Param("docId") Long docId);

}
