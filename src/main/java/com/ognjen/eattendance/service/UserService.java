package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<Student> getStudentByUsername(String username);
    Optional<Professor> getProfessorByUsername(String username);
    List<Student> getAllStudents();
    Optional<Student> getStudentById(Long id);
    void saveStudent(Student student);
    void deleteStudentById(Long id);
    List<Professor> getAllProfessors();
    Optional<Professor> getProfessorById(Long id);
    void saveProfessor(Professor professor);
    void deleteProfessorById(Long id);
}
