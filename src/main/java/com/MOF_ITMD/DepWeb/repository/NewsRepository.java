package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByOrderByCreatedAtDesc();

    List<News> findTop5ByStatusOrderByCreatedAtDesc(int status);


}

