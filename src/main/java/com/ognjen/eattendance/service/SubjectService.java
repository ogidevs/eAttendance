package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Subject> findSubjectsByProfessorId(Long professorId) {
        return subjectRepository.findAll().stream()
                .filter(subject -> subject.getProfessor() != null && subject.getProfessor().getId().equals(professorId))
                .collect(Collectors.toList());
    }

    public List<Subject> findSubjectsByStudentId(Long studentId) {
        return subjectRepository.findAll().stream()
                .filter(subject -> subject.getEnrolledStudents().stream()
                        .anyMatch(student -> student.getId().equals(studentId)))
                .collect(Collectors.toList());
    }

    public void save(Subject subject) { subjectRepository.save(subject); }
    public void deleteById(Long id) { subjectRepository.deleteById(id); }
    public Optional<Subject> findById(Long id) { return subjectRepository.findById(id); }
}