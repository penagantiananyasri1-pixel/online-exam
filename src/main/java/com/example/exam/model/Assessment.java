package com.example.exam.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name="assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    private Subject subject;

    private boolean published = false;

    @OneToMany(mappedBy="assessment",
        cascade=CascadeType.ALL,
        orphanRemoval=true)
    @OrderBy("id ASC")
    private List<Question> questions = new ArrayList<>();

    public Assessment() {}

    public Assessment(String title, Subject subject) {
        this.title = title;
        this.subject = subject;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Subject getSubject() { return subject; }
    public boolean isPublished() { return published; }
    public List<Question> getQuestions() { return questions; }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
