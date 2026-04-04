package com.LawEZY.content.repository;

import com.LawEZY.content.entity.LegalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<LegalResource, Long> {
    List<LegalResource> findByCategoryIgnoreCase(String category);
    List<LegalResource> findByTitleContainingIgnoreCase(String keyword);
}
