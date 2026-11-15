package com.example.exam.controller;

import com.example.exam.model.*;
import com.example.exam.repository.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository students;
    private final AssessmentRepository assessments;
    private final QuestionRepository questions;
    private final ExamResultRepository results;

    public StudentController(
            StudentRepository students,
            AssessmentRepository assessments,
            QuestionRepository questions,
            ExamResultRepository results) {

        this.students = students;
        this.assessments = assessments;
        this.questions = questions;
        this.results = results;
    }

    @GetMapping("/login")
    public String login() {
        return "student-login";
    }

    @PostMapping("/login")
    public String studentLogin(
            @RequestParam String name,
            @RequestParam String rollNumber,
            HttpSession session,
            RedirectAttributes redirect) {

        if (name.isBlank() || rollNumber.isBlank()) {
            redirect.addFlashAttribute(
                    "error", "Enter name and roll number.");
            return "redirect:/student/login";
        }

        Student student = students
                .findByRollNumber(rollNumber.trim())
                .orElseGet(() ->
                        students.save(
                                new Student(
                                        name.trim(),
                                        rollNumber.trim())));

        student.setName(name.trim());
        students.save(student);

        session.setAttribute(
                "studentRoll", student.getRollNumber());

        return "redirect:/student/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session, Model model) {

        String roll =
                (String) session.getAttribute("studentRoll");

        if (roll == null) {
            return "redirect:/student/login";
        }

        Student student =
                students.findByRollNumber(roll).orElseThrow();

        model.addAttribute("student", student);

        model.addAttribute(
                "assessments",
                assessments.findByPublishedTrueOrderByIdDesc());

        model.addAttribute(
                "history",
                results.findByStudentRollNumberOrderBySubmittedAtDesc(
                        roll));

        return "student-dashboard";
    }

    @GetMapping("/exam/{id}")
    public String exam(
            @PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {

        if (session.getAttribute("studentRoll") == null) {
            return "redirect:/student/login";
        }

        Assessment assessment =
                assessments.findById(id).orElseThrow();

        if (!assessment.isPublished()) {
            redirect.addFlashAttribute(
                    "error", "Assessment is not available.");

            return "redirect:/student/dashboard";
        }

        List<Question> paper =
                questions.findByAssessmentIdOrderByIdAsc(id);

        model.addAttribute("assessment", assessment);
        model.addAttribute("questions", paper);

        return "exam";
    }

    @PostMapping("/exam/{id}/submit")
    public String submitExam(
            @PathVariable Long id,
            @RequestParam Map<String, String> form,
            HttpSession session,
            Model model) {

        String roll =
                (String) session.getAttribute("studentRoll");

        if (roll == null) {
            return "redirect:/student/login";
        }

        Assessment assessment =
                assessments.findById(id).orElseThrow();

        if (!assessment.isPublished()) {
            return "redirect:/student/dashboard";
        }

        Student student =
                students.findByRollNumber(roll).orElseThrow();

        List<Question> paper =
                questions.findByAssessmentIdOrderByIdAsc(id);

        int score = 0;
        int total = 0;

        for (Question q : paper) {

            total += q.getMarks();

            String answer =
                    form.get("q" + q.getId());

            if (answer != null &&
                    answer.equalsIgnoreCase(
                            q.getCorrectOption())) {

                score += q.getMarks();
            }
        }

        results.save(
                new ExamResult(
                        student, assessment, score, total));

        model.addAttribute("student", student);
        model.addAttribute("assessment", assessment);
        model.addAttribute("score", score);
        model.addAttribute("total", total);

        model.addAttribute(
                "history",
                results.findByStudentRollNumberOrderBySubmittedAtDesc(
                        roll));

        return "result";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("studentRoll");
        return "redirect:/";
    }
}
