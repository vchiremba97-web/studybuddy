package com.studybuddy.controller;

import com.studybuddy.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Display registration page
    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }

    // Process registration form
    @PostMapping("/register")
    public String registerStudent(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String educationLevel,
            @RequestParam String institutionName,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            studentService.registerStudent(
                    fullName,
                    email,
                    password,
                    educationLevel,
                    institutionName
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your account has been created! You can now log in."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException exception) {

            model.addAttribute("errorMessage", exception.getMessage());

            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("educationLevel", educationLevel);
            model.addAttribute("institutionName", institutionName);

            return "register";
        }
    }

    // Display login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
