package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Subject;

import java.util.List;
import java.util.Optional;

public interface SubjectService {
    List<Subject> getAllSubjects();
    List<Subject> findSubjectsByProfessorId(Long professorId);
    List<Subject> findSubjectsByStudentId(Long studentId);
    void save(Subject subject);
    void deleteById(Long id);
    Optional<Subject> findById(Long id);
}
