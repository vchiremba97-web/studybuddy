
package com.studybuddy.controller;

import com.studybuddy.model.Student;
import com.studybuddy.service.PremiumService;
import com.studybuddy.service.StudentService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PremiumController {

    private final StudentService studentService;
    private final PremiumService premiumService;

    public PremiumController(
            StudentService studentService,
            PremiumService premiumService) {

        this.studentService = studentService;
        this.premiumService = premiumService;
    }

    private Student getCurrentStudent(UserDetails userDetails) {

        if (userDetails == null) {
            throw new IllegalStateException(
                    "No student is logged in."
            );
        }

        Student student = studentService.findByEmail(
                userDetails.getUsername()
        );

        if (student == null) {
            throw new IllegalStateException(
                    "Student account not found."
            );
        }

        return student;
    }

    @GetMapping("/premium")
    public String premium(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        student = premiumService.getMembershipDetails(student.getId());

        model.addAttribute("studentName", student.getFullName());
        model.addAttribute("student", student);
        model.addAttribute("isPremium", student.isPremium());

        return "premium";
    }

    @PostMapping("/premium/upgrade")
    public String upgrade(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Student student = getCurrentStudent(userDetails);

            premiumService.upgradeToPremium(student);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Congratulations! Your 30-day Premium membership is now active."
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not activate Premium: " + exception.getMessage()
            );
        }

        return "redirect:/premium";
    }

    @PostMapping("/premium/cancel")
    public String cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Student student = getCurrentStudent(userDetails);

            premiumService.cancelPremium(student);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your Premium membership has been cancelled."
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not cancel Premium: " + exception.getMessage()
            );
        }

        return "redirect:/premium";
    }
}
