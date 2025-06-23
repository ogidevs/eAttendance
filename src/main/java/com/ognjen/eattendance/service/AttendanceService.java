package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;

import java.util.List;

public interface AttendanceService {
    String generateAttendanceCodeForClass(Long classId);
    boolean isCodeValid(String code);
    List<ScheduledClass> getAvailableClassesForStudent(Long studentId);
    String recordAttendance(String code, Long studentId);
    List<AttendanceRecord> getAttendanceHistoryForStudent(Long studentId);
    List<AttendanceRecord> getAttendanceHistoryForClass(Long classId);
}
