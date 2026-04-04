package com.LawEZY.content.service;

import com.LawEZY.content.entity.LegalResource;
import com.LawEZY.content.repository.ResourceRepository;
import com.LawEZY.content.dto.ResourceRequest;

import com.LawEZY.user.entity.User;
import com.LawEZY.user.repository.UserRepository;
import com.LawEZY.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<LegalResource> getAllResources(String category) {
        if (category != null) {
            return resourceRepository.findByCategoryIgnoreCase(category);
        }
        return resourceRepository.findAll();
    }

    public LegalResource updateResource(Long resourceId, String content) {
        LegalResource res = resourceRepository.findById(resourceId).get();
        res.setContent(content);
        return resourceRepository.save(res);
    }

    public LegalResource getResourceById(@NonNull Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public LegalResource createResource(@NonNull ResourceRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        
        LegalResource resource = new LegalResource();
        resource.setTitle(request.getTitle());
        resource.setContent(request.getContent());
        resource.setCategory(request.getCategory());
        resource.setAuthor(author);
        return resourceRepository.save(resource);
    }

    public void deleteResource(@NonNull Long id) {
        LegalResource resource = getResourceById(id);
        resourceRepository.delete(resource);
    }
}
