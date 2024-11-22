package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findByContentName(String contentName);
}
