package com.ognjen.eattendance.model;

import lombok.Data;

import java.util.List;

@Data
public class Subject {
    private Long id;
    private String name;
    private String code;
    private Professor professor;
    private List<Student> enrolledStudents;
}