package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.AttendanceRecord;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class AttendanceRecordRepository {

    private final List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public AttendanceRecord save(AttendanceRecord record) {
        record.setId(idCounter.incrementAndGet());
        attendanceRecords.add(record);
        return record;
    }

    public List<AttendanceRecord> findByClassId(Long classId) {
        return attendanceRecords.stream()
                .filter(ar -> ar.getScheduledClass().getId().equals(classId))
                .collect(Collectors.toList());
    }

    public boolean existsByClassIdAndStudentId(Long classId, Long studentId) {
        return attendanceRecords.stream()
                .anyMatch(ar -> ar.getScheduledClass().getId().equals(classId) &&
                        ar.getStudent().getId().equals(studentId));
    }
}