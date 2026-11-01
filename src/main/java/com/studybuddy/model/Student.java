
package com.studybuddy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String educationLevel;

    private String institutionName;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String membershipType = "FREE";

    private LocalDateTime premiumStartDate;

    private LocalDateTime premiumExpiryDate;

    public Student() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (membershipType == null || membershipType.isBlank()) {
            membershipType = "FREE";
        }
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public LocalDateTime getPremiumStartDate() {
        return premiumStartDate;
    }

    public void setPremiumStartDate(LocalDateTime premiumStartDate) {
        this.premiumStartDate = premiumStartDate;
    }

    public LocalDateTime getPremiumExpiryDate() {
        return premiumExpiryDate;
    }

    public void setPremiumExpiryDate(LocalDateTime premiumExpiryDate) {
        this.premiumExpiryDate = premiumExpiryDate;
    }

    public boolean isPremium() {
        return "PREMIUM".equals(membershipType)
                && premiumExpiryDate != null
                && premiumExpiryDate.isAfter(LocalDateTime.now());
    }
}
