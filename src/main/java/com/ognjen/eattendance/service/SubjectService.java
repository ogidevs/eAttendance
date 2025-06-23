package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.repository.InMemoryDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final InMemoryDataRepository repository;

    /**
     * Vraća listu svih predmeta u sistemu.
     * @return Lista predmeta.
     */
    public List<Subject> getAllSubjects() {
        return repository.findAllSubjects();
    }

    /**
     * Pronalazi sve predmete koje predaje profesor sa datim ID-jem.
     * @param professorId ID profesora.
     * @return Lista predmeta koje profesor predaje.
     */
    public List<Subject> findSubjectsByProfessorId(Long professorId) {
        return repository.findAllSubjects().stream()
                .filter(subject -> subject.getProfessor() != null && subject.getProfessor().getId().equals(professorId))
                .collect(Collectors.toList());
    }

    /**
     * Pronalazi sve predmete koje sluša student sa datim ID-jem.
     * @param studentId ID studenta.
     * @return Lista predmeta koje student sluša.
     */
    public List<Subject> findSubjectsByStudentId(Long studentId) {
        return repository.findAllSubjects().stream()
                .filter(subject -> subject.getEnrolledStudents().stream()
                        .anyMatch(student -> student.getId().equals(studentId)))
                .collect(Collectors.toList());
    }
}