package com.ognjen.eattendance.service;
import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.ProfessorRepository;
import com.ognjen.eattendance.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private UserService userService;
    private SubjectService subjectService;
    private StudentRepository studentRepository;
    private ProfessorRepository professorRepository;

    @BeforeEach
    void setUp() {
        studentRepository = new StudentRepository();
        professorRepository = new ProfessorRepository();
        userService = new UserServiceImpl(studentRepository, professorRepository, subjectService);

        // Dodavanje test korisnika
        Student s1 = new Student();
        s1.setUsername("stud.markovic");
        s1.setPassword("pass123");
        studentRepository.save(s1);

        Professor p1 = new Professor();
        p1.setUsername("prof.petrovic");
        p1.setPassword("pass456");
        professorRepository.save(p1);
    }

    @Test
    @DisplayName("Test slučaja prijave: Pronalazi postojećeg studenta po korisničkom imenu")
    void getStudentByUsername_ShouldReturnStudent_WhenExists() {
        // Act
        Optional<Student> foundStudent = userService.getStudentByUsername("stud.markovic");

        // Assert
        assertTrue(foundStudent.isPresent());
        assertEquals("stud.markovic", foundStudent.get().getUsername());
    }

    @Test
    @DisplayName("Test slučaja prijave: Ne pronalazi nepostojećeg studenta")
    void getStudentByUsername_ShouldReturnEmpty_WhenNotExists() {
        // Act
        Optional<Student> foundStudent = userService.getStudentByUsername("nepostojeci.student");

        // Assert
        assertFalse(foundStudent.isPresent());
    }

    @Test
    @DisplayName("Test slučaja prijave: Pronalazi postojećeg profesora po korisničkom imenu")
    void getProfessorByUsername_ShouldReturnProfessor_WhenExists() {
        // Act
        Optional<Professor> foundProfessor = userService.getProfessorByUsername("prof.petrovic");

        // Assert
        assertTrue(foundProfessor.isPresent());
        assertEquals("prof.petrovic", foundProfessor.get().getUsername());
    }

    @Test
    @DisplayName("Test čuvanja novog studenta")
    void saveStudent_ShouldAddStudentToRepository() {
        // Arrange
        Student newStudent = new Student();
        newStudent.setUsername("novi.student");
        newStudent.setPassword("sigurnaLozinka");

        // Act
        userService.saveStudent(newStudent);

        // Assert
        Optional<Student> savedStudent = studentRepository.findByUsername("novi.student");
        assertTrue(savedStudent.isPresent());
        assertNotNull(savedStudent.get().getId()); // Provera da li je ID dodeljen
    }
}