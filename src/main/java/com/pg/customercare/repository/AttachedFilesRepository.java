package com.pg.customercare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pg.customercare.model.AttachedFiles;

public interface AttachedFilesRepository extends JpaRepository<AttachedFiles, Long> {
    
}
