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
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    /**
     * Vraća listu svih predmeta iz baze podataka.
     * @return lista svih {@link Subject} entiteta
     */
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    /**
     * Pronalazi sve predmete koje predaje profesor sa zadatim ID-jem.
     * @param professorId ID profesora
     * @return lista {@link Subject} entiteta koje predaje dati profesor
     */
    public List<Subject> findSubjectsByProfessorId(Long professorId) {
        return subjectRepository.findAll().stream()
                .filter(subject -> subject.getProfessor() != null && subject.getProfessor().getId().equals(professorId))
                .collect(Collectors.toList());
    }

    /**
     * Pronalazi sve predmete na kojima je student sa zadatim ID-jem upisan.
     * @param studentId ID studenta
     * @return lista {@link Subject} entiteta na kojima je student upisan
     */
    public List<Subject> findSubjectsByStudentId(Long studentId) {
        return subjectRepository.findAll().stream()
                .filter(subject -> subject.getEnrolledStudents().stream()
                        .anyMatch(student -> student.getId().equals(studentId)))
                .collect(Collectors.toList());
    }

    /**
     * Čuva novi ili ažurira postojeći predmet u bazi podataka.
     * @param subject predmet koji se čuva
     */
    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    /**
     * Briše predmet sa zadatim ID-jem iz baze podataka.
     * @param id ID predmeta koji se briše
     */
    public void deleteById(Long id) {
        subjectRepository.deleteById(id);
    }

    /**
     * Pronalazi predmet na osnovu njegovog ID-ja.
     * @param id ID predmeta
     * @return {@link Optional} koji sadrži pronađeni predmet, ili prazan ako ne postoji
     */
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }
}