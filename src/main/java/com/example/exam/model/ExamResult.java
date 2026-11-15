package com.example.exam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="exam_results")
public class ExamResult {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Assessment assessment;

    private int score;
    private int totalMarks;
    private LocalDateTime submittedAt;

    public ExamResult() {}

    public ExamResult(Student student,
        Assessment assessment, int score, int totalMarks) {

        this.student = student;
        this.assessment = assessment;
        this.score = score;
        this.totalMarks = totalMarks;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Assessment getAssessment() { return assessment; }
    public int getScore() { return score; }
    public int getTotalMarks() { return totalMarks; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
}
