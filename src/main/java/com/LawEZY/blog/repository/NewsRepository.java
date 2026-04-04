package com.LawEZY.blog.repository;

import com.LawEZY.blog.entity.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsItem, Long> {
    List<NewsItem> findAllByOrderByPublishedAtDesc();
    boolean existsByTitle(String title);
}
