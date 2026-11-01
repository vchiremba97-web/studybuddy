package com.studybuddy.config;

import com.studybuddy.model.Student;
import com.studybuddy.service.StudentService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentService studentService;

    public CustomUserDetailsService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Student student = studentService.findByEmail(email);

        if (student == null) {
            throw new UsernameNotFoundException(
                    "No student account found with email: " + email
            );
        }

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .roles("STUDENT")
                .build();
    }
}
