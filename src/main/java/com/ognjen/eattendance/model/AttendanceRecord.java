package com.ognjen.eattendance.model;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceRecord {
    private Long id;
    private Student student;
    private ScheduledClass scheduledClass;
    private LocalDateTime checkInTime;
}