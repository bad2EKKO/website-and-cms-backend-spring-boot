package com.MOF_ITMD.DepWeb.repository;

import com.MOF_ITMD.DepWeb.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByAddedEventAtDesc();
}
