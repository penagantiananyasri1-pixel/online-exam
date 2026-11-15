package com.example.exam.controller;

import com.example.exam.model.*;
import com.example.exam.repository.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Value("${app.admin.password}")
    private String adminPassword;

    private final SubjectRepository subjects;
    private final AssessmentRepository assessments;
    private final QuestionRepository questions;

    public AdminController(
            SubjectRepository subjects,
            AssessmentRepository assessments,
            QuestionRepository questions) {

        this.subjects = subjects;
        this.assessments = assessments;
        this.questions = questions;
    }

    @GetMapping("/login")
    public String login() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirect) {

        if (adminPassword.equals(password)) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin/dashboard";
        }

        redirect.addFlashAttribute(
                "error", "Incorrect admin password!");

        return "redirect:/admin/login";
    }

    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(
                session.getAttribute("adminLoggedIn"));
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session, Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute(
                "assessments",
                assessments.findAllByOrderByIdDesc());

        return "admin-dashboard";
    }

    @PostMapping("/assessment")
    public String createAssessment(
            @RequestParam String subjectName,
            @RequestParam String title,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        if (subjectName.isBlank() || title.isBlank()) {
            redirect.addFlashAttribute(
                    "error", "Enter subject and assessment title.");
            return "redirect:/admin/dashboard";
        }

        Subject subject = subjects
                .findByNameIgnoreCase(subjectName.trim())
                .orElseGet(() ->
                        subjects.save(
                                new Subject(subjectName.trim())));

        Assessment assessment =
                new Assessment(title.trim(), subject);

        assessments.save(assessment);

        redirect.addFlashAttribute(
                "message", "Assessment created successfully!");

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/assessment/{id}")
    public String editAssessment(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        Assessment assessment =
                assessments.findById(id).orElseThrow();

        model.addAttribute("assessment", assessment);

        model.addAttribute(
                "questions",
                questions.findByAssessmentIdOrderByIdAsc(id));

        return "admin-questions";
    }

    @PostMapping("/assessment/{id}/question")
    public String addQuestion(
            @PathVariable Long id,
            @RequestParam String questionText,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam String correctOption,
            @RequestParam int marks,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        Assessment assessment =
                assessments.findById(id).orElseThrow();

        String answer = correctOption.toUpperCase();

        if (!answer.matches("[ABCD]")
                || marks < 1
                || questionText.isBlank()) {

            redirect.addFlashAttribute(
                    "error", "Invalid question details.");

            return "redirect:/admin/assessment/" + id;
        }

        Question question = new Question(
                assessment,
                questionText,
                optionA,
                optionB,
                optionC,
                optionD,
                answer,
                marks
        );

        questions.save(question);

        redirect.addFlashAttribute(
                "message", "Question added successfully!");

        return "redirect:/admin/assessment/" + id;
    }

    @PostMapping("/assessment/{id}/publish")
    public String publishAssessment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirect) {

        if (!isAdmin(session)) {
            return "redirect:/admin/login";
        }

        Assessment assessment =
                assessments.findById(id).orElseThrow();

        if (questions.findByAssessmentIdOrderByIdAsc(id).isEmpty()) {
            redirect.addFlashAttribute(
                    "error", "Add at least one question first.");
        } else {
            assessment.setPublished(true);
            assessments.save(assessment);

            redirect.addFlashAttribute(
                    "message", "Question paper published!");
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLoggedIn");
        return "redirect:/";
    }
}
