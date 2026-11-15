package com.example.exam.repository;

import com.example.exam.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {

    List<Assessment> findByPublishedTrueOrderByIdDesc();

    List<Assessment> findAllByOrderByIdDesc();
}