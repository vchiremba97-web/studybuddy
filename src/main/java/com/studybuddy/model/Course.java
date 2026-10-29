
package com.studybuddy.model;

public class Course {

    private Long id;
    private String name;
    private String code;
    private String instructor;
    private String description;

    public Course() {
    }

    public Course(Long id, String name, String code,
                  String instructor, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.instructor = instructor;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
