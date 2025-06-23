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
public class UserServiceImpl implements UserService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    // --- METODE ZA PRONALAŽENJE KORISNIKA (ZA LOGIN) ---

    /**
     * Pronalazi studenta na osnovu korisničkog imena.
     * @param username korisničko ime studenta
     * @return {@link Optional} koji sadrži pronađenog studenta, ili prazan ako ne postoji
     */
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    /**
     * Pronalazi profesora na osnovu korisničkog imena.
     * @param username korisničko ime profesora
     * @return {@link Optional} koji sadrži pronađenog profesora, ili prazan ako ne postoji
     */
    public Optional<Professor> getProfessorByUsername(String username) {
        return professorRepository.findByUsername(username);
    }

// --- STUDENT CRUD METODE (ZA ADMINA) ---

    /**
     * Vraća listu svih studenata.
     * @return lista svih {@link Student} entiteta
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Pronalazi studenta na osnovu ID-ja.
     * @param id ID studenta
     * @return {@link Optional} koji sadrži pronađenog studenta, ili prazan ako ne postoji
     */
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Čuva novog ili ažurira postojećeg studenta.
     * @param student student koji se čuva
     */
    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    /**
     * Briše studenta sa zadatim ID-jem.
     * @param id ID studenta koji se briše
     */
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }

// --- PROFESSOR CRUD METODE (ZA ADMINA) ---

    /**
     * Vraća listu svih profesora.
     * @return lista svih {@link Professor} entiteta
     */
    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }

    /**
     * Pronalazi profesora na osnovu ID-ja.
     * @param id ID profesora
     * @return {@link Optional} koji sadrži pronađenog profesora, ili prazan ako ne postoji
     */
    public Optional<Professor> getProfessorById(Long id) {
        return professorRepository.findById(id);
    }

    /**
     * Čuva novog ili ažurira postojećeg profesora.
     * @param professor profesor koji se čuva
     */
    public void saveProfessor(Professor professor) {
        professorRepository.save(professor);
    }

    /**
     * Briše profesora sa zadatim ID-jem.
     * @param id ID profesora koji se briše
     */
    public void deleteProfessorById(Long id) {
        professorRepository.deleteById(id);
    }
}