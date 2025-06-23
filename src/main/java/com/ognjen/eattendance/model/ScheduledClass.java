package com.ognjen.eattendance.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduledClass {
    private Long id;
    private Subject subject;
    private LocalDateTime classDateTime;
    private Integer duration; // u minutama
    private String attendanceCode;
    private LocalDateTime codeActiveUntil;
}