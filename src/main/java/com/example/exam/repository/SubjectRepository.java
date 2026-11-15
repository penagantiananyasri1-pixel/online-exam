package com.example.exam.repository;

import com.example.exam.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubjectRepository
        extends JpaRepository<Subject, Long> {
    Optional<Subject> findByNameIgnoreCase(String name);
}