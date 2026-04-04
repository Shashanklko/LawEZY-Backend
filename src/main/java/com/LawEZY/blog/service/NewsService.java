package com.LawEZY.blog.service;

import com.LawEZY.blog.entity.NewsItem;
import com.LawEZY.blog.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    // Fetch news automatically every 6 hours
    @Scheduled(fixedRate = 21600000) 
    public void autoFetchNews() {
        System.out.println("--- Starting Legal News Update ---");
        
        String title = "Supreme Court Issues New Guidelines on Privacy";
        if (newsRepository.existsByTitle(title)) {
            System.out.println("News item already exists. Skipping.");
            return;
        }

        NewsItem sample = new NewsItem();
        sample.setTitle(title);
        sample.setSummary("The High Court has introduced a sweeping set of changes...");
        sample.setPublishedAt(LocalDateTime.now());
        sample.setSourceUrl("https://legal-news-source.com/article/1");
        
        newsRepository.save(sample);
    }

    public List<NewsItem> getLatestNews() {
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }
}
