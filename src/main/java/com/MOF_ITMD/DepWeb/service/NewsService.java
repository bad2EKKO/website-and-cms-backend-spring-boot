package com.MOF_ITMD.DepWeb.service;

import com.MOF_ITMD.DepWeb.dto.UpdateNewsDTO;
import com.MOF_ITMD.DepWeb.dto.UpdateStatusDTO;
import com.MOF_ITMD.DepWeb.models.News;
import com.MOF_ITMD.DepWeb.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public News addNews(News data) {
        data.setCreatedAt(LocalDateTime.now());
        data.setLatestUpdateAt(LocalDateTime.now());
        return newsRepository.save(data);
    }

    public List<News> getAllNewsOrderedByDate() {
        return newsRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<News> getLast5ActiveNews() {
        return newsRepository.findTop5ByStatusOrderByCreatedAtDesc(1);
    }

    public News updateNews(Long id, UpdateNewsDTO updateNewsDTO) {
        News news = getNewsById(id);
        updateNewsDTO.setLatestUpdate(LocalDateTime.now());
        news.setTitle(updateNewsDTO.getTitle());
        news.setContent(updateNewsDTO.getContent());
        news.setLatestUpdateAt(updateNewsDTO.getLatestUpdate());
        return newsRepository.save(news);
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    public void deleteNews(Long id) {
        News news = getNewsById(id);
        newsRepository.delete(news);
    }

    public News updateNewsStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        News tempNews = getNewsById(id);

        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempNews.setStatus(updateStatusDTO.getStatus());
        tempNews.setLatestUpdateAt(updateStatusDTO.getUpdateAt());
        return newsRepository.save(tempNews);
    }
}
