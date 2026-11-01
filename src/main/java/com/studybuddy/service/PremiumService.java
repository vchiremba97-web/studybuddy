
package com.studybuddy.service;

import com.studybuddy.model.Student;
import com.studybuddy.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class PremiumService {

    private final StudentRepository studentRepository;

    public PremiumService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student upgradeToPremium(Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A valid student account is required."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        boolean alreadyPremium = student.isPremium();

        LocalDateTime expiryDate;

        if (alreadyPremium) {
            expiryDate = student.getPremiumExpiryDate().plusDays(30);
        } else {
            student.setPremiumStartDate(now);
            expiryDate = now.plusDays(30);
        }

        student.setMembershipType("PREMIUM");
        student.setPremiumExpiryDate(expiryDate);

        return studentRepository.save(student);
    }

    public boolean hasPremiumAccess(Student student) {

        if (student == null || student.getId() == null) {
            return false;
        }

        if (student.isPremium()) {
            return true;
        }

        if ("PREMIUM".equals(student.getMembershipType())) {
            student.setMembershipType("FREE");
            student.setPremiumStartDate(null);
            student.setPremiumExpiryDate(null);

            studentRepository.save(student);
        }

        return false;
    }

    public Student getMembershipDetails(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student account not found."
                ));

        hasPremiumAccess(student);

        return student;
    }

    public Student cancelPremium(Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A valid student account is required."
            );
        }

        student.setMembershipType("FREE");
        student.setPremiumStartDate(null);
        student.setPremiumExpiryDate(null);

        return studentRepository.save(student);
    }
}