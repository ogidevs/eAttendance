package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.ProfessorRepository;
import com.ognjen.eattendance.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    // --- METODE ZA PRONALAŽENJE KORISNIKA (ZA LOGIN) ---

    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public Optional<Professor> getProfessorByUsername(String username) {
        return professorRepository.findByUsername(username);
    }

    // --- STUDENT CRUD METODE (ZA ADMINA) ---

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

    // --- PROFESSOR CRUD METODE (ZA ADMINA) ---

    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    public Optional<Professor> getProfessorById(Long id) {
        return professorRepository.findById(id);
    }

    public void saveProfessor(Professor professor) {
        professorRepository.save(professor);
    }

    public void deleteProfessorById(Long id) {
        professorRepository.deleteById(id);
    }
}