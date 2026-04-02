package com.LawEZY.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LawEZY.user.entity.LawyerProfile;

@Repository
public interface LawyerProfileRepository extends JpaRepository<LawyerProfile, Long> {
    
}
