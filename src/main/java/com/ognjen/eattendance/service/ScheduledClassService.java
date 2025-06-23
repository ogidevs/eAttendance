package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.ScheduledClass;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduledClassService {
    void save(ScheduledClass sc);
    List<ScheduledClass> findClassesByProfessorId(Long professorId);
    List<ScheduledClass> findClassesByStudentId(Long studentID);
    Optional<ScheduledClass> findById(Long classId);
    void createNewClass(Long subjectId, LocalDateTime classDateTime, Integer duration);
    Optional<ScheduledClass> findByAttendanceCode(String code);
    void deleteClass(Long classId);
}
