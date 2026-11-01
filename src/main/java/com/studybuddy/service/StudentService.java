package com.studybuddy.service;

import com.studybuddy.model.Student;
import com.studybuddy.repository.StudentRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student registerStudent(
            String fullName,
            String email,
            String password,
            String educationLevel,
            String institutionName) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Please enter your full name.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Please enter your email.");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Your password must contain at least 8 characters.");
        }

        if (educationLevel == null ||
                !(educationLevel.equalsIgnoreCase("HIGH_SCHOOL")
                        || educationLevel.equalsIgnoreCase("UNIVERSITY"))) {
            throw new IllegalArgumentException(
                    "Please select high school or university.");
        }

        if (institutionName == null || institutionName.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter your school or university name.");
        }

        String cleanEmail = email.trim().toLowerCase(Locale.ROOT);

        if (studentRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        Student student = new Student();

        student.setFullName(fullName.trim());
        student.setEmail(cleanEmail);
        student.setPassword(passwordEncoder.encode(password));
        student.setEducationLevel(educationLevel.toUpperCase(Locale.ROOT));
        student.setInstitutionName(institutionName.trim());

        return studentRepository.save(student);
    }

    public Student findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return studentRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT)
        ).orElse(null);
    }
}
