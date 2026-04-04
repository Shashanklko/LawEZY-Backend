package com.LawEZY.blog.controller;

import com.LawEZY.blog.entity.NewsItem;
import com.LawEZY.blog.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/latest")
    public ResponseEntity<List<NewsItem>> getLatestNews() {
        return ResponseEntity.ok(newsService.getLatestNews());
    }
}
