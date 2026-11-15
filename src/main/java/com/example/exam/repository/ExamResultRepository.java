package com.example.exam.repository;

import com.example.exam.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExamResultRepository
        extends JpaRepository<ExamResult, Long> {

    List<ExamResult>
    findByStudentRollNumberOrderBySubmittedAtDesc(
        String rollNumber);
}