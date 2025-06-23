package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.ScheduledClass;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ScheduledClassRepository {

    private final List<ScheduledClass> scheduledClasses = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<ScheduledClass> findAll() {
        return new ArrayList<>(scheduledClasses);
    }

    public Optional<ScheduledClass> findById(Long id) {
        return scheduledClasses.stream()
                .filter(sc -> sc.getId().equals(id))
                .findFirst();
    }

    public Optional<ScheduledClass> findByAttendanceCode(String code) {
        return scheduledClasses.stream()
                .filter(sc -> code.equals(sc.getAttendanceCode()))
                .findFirst();
    }

    public List<ScheduledClass> findBySubjectId(Long subjectId) {
        return scheduledClasses.stream()
                .filter(sc -> sc.getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());
    }

    public List<ScheduledClass> findByProfessorId(Long professorId) {
        return scheduledClasses.stream()
                .filter(sc -> sc.getSubject().getProfessor() != null &&
                        sc.getSubject().getProfessor().getId().equals(professorId))
                .collect(Collectors.toList());
    }

    public List<ScheduledClass> findClassesByStudentId(Long studentId) {
        return scheduledClasses.stream()
                .filter(sc -> sc.getSubject().getEnrolledStudents().stream()
                        .anyMatch(student -> student.getId().equals(studentId)))
                .collect(Collectors.toList());
    }

    public ScheduledClass save(ScheduledClass scheduledClass) {
        if (scheduledClass.getId() == null) {
            scheduledClass.setId(idCounter.incrementAndGet());
            scheduledClasses.add(scheduledClass);
        } else {
            findById(scheduledClass.getId()).ifPresent(existing -> {
                int index = scheduledClasses.indexOf(existing);
                scheduledClasses.set(index, scheduledClass);
            });
        }
        return scheduledClass;
    }
}