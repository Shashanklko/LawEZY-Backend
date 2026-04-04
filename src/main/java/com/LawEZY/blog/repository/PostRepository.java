package com.LawEZY.blog.repository;

import com.LawEZY.blog.entity.Post;
import com.LawEZY.blog.enums.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTypeOrderByCreatedAtDesc(PostType type);
    List<Post> findAllByOrderByCreatedAtDesc();
}
