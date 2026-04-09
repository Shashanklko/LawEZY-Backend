package com.LawEZY.blog.service;

import com.LawEZY.blog.entity.NewsItem;
import com.LawEZY.blog.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@lombok.extern.slf4j.Slf4j
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // Fetch news automatically every 6 hours
    @Scheduled(fixedRate = 21600000) 
    public void autoFetchNews() {
        log.info("[NEWS] Starting Legal News Update...");
        
        String title = "Supreme Court Issues New Guidelines on Privacy";
        if (newsRepository.existsByTitle(title)) {
            log.info("[NEWS] News item already exists. Skipping.");
            return;
        }

        NewsItem sample = new NewsItem();
        sample.setTitle(title);
        sample.setSummary("The High Court has introduced a sweeping set of changes...");
        sample.setPublishedAt(LocalDateTime.now());
        sample.setSourceUrl("https://legal-news-source.com/article/1");
        
        newsRepository.save(sample);
        log.info("[NEWS] Saved NEW news item: {}", title);
    }

    public List<NewsItem> getLatestNews() {
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }
}
