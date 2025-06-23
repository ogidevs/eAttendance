package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryDataRepository {

    // "Tabele" u našoj bazi
    private final List<Student> students = new ArrayList<>();
    private final List<Professor> professors = new ArrayList<>();
    private final List<Subject> subjects = new ArrayList<>();
    private final List<ScheduledClass> scheduledClasses = new ArrayList<>();
    private final List<AttendanceRecord> attendanceRecords = new ArrayList<>();

    // Brojači za generisanje jedinstvenih ID-jeva
    private final AtomicLong studentIdCounter = new AtomicLong(0);
    private final AtomicLong professorIdCounter = new AtomicLong(0);
    private final AtomicLong subjectIdCounter = new AtomicLong(0);
    private final AtomicLong classIdCounter = new AtomicLong(0);
    private final AtomicLong attendanceIdCounter = new AtomicLong(0);

    // Metoda koja se poziva čim se aplikacija pokrene da bi se ubacili testni podaci
    @PostConstruct
    public void init() {
        // Kreiranje profesora
        Professor p1 = new Professor();
        p1.setId(professorIdCounter.incrementAndGet());
        p1.setUsername("prof.petrovic");
        p1.setPassword("pass");
        p1.setFirstName("Petar");
        p1.setLastName("Petrović");
        p1.setTitle("Dr");
        professors.add(p1);

        // Kreiranje studenata
        Student s1 = new Student();
        s1.setId(studentIdCounter.incrementAndGet());
        s1.setUsername("stud.markovic");
        s1.setPassword("pass");
        s1.setFirstName("Marko");
        s1.setLastName("Marković");
        s1.setIndexNumber("IT 15/2020");
        students.add(s1);

        Student s2 = new Student();
        s2.setId(studentIdCounter.incrementAndGet());
        s2.setUsername("stud.ilic");
        s2.setPassword("pass");
        s2.setFirstName("Ilija");
        s2.setLastName("Ilić");
        s2.setIndexNumber("IT 22/2020");
        students.add(s2);

        // Kreiranje predmeta i povezivanje
        Subject sub1 = new Subject();
        sub1.setId(subjectIdCounter.incrementAndGet());
        sub1.setName("Veb Sistemi 1");
        sub1.setCode("IT355");
        sub1.setProfessor(p1);
        sub1.setEnrolledStudents(List.of(s1, s2));
        subjects.add(sub1);

        // Kreiranje zakazanog časa
        ScheduledClass sc1 = new ScheduledClass();
        sc1.setId(classIdCounter.incrementAndGet());
        sc1.setSubject(sub1);
        sc1.setClassDateTime(LocalDateTime.now().minusDays(1)); // Juče
        scheduledClasses.add(sc1);
    }

    // --- Metode za CRUD operacije ---

    // STUDENT
    public List<Student> findAllStudents() {
        return new ArrayList<>(students);
    }

    public Optional<Student> findStudentById(Long id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public Optional<Student> findStudentByUsername(String username) {
        return students.stream().filter(s -> s.getUsername().equals(username)).findFirst();
    }

    // PROFESSOR
    public List<Professor> findAllProfessors() {
        return new ArrayList<>(professors);
    }

    public Optional<Professor> findProfessorById(Long id) {
        return professors.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Optional<Professor> findProfessorByUsername(String username) {
        return professors.stream().filter(p -> p.getUsername().equals(username)).findFirst();
    }

    // SUBJECT
    public List<Subject> findAllSubjects() {
        return new ArrayList<>(subjects);
    }

    // SCHEDULED CLASS
    public List<ScheduledClass> findAllScheduledClasses() {
        return new ArrayList<>(scheduledClasses);
    }

    public Optional<ScheduledClass> findScheduledClassById(Long id) {
        return scheduledClasses.stream().filter(sc -> sc.getId().equals(id)).findFirst();
    }

    public Optional<ScheduledClass> findScheduledClassByCode(String code) {
        return scheduledClasses.stream()
                .filter(sc -> code.equals(sc.getAttendanceCode()))
                .findFirst();
    }

    public ScheduledClass saveScheduledClass(ScheduledClass scheduledClass) {
        if (scheduledClass.getId() == null) {
            scheduledClass.setId(classIdCounter.incrementAndGet());
            scheduledClasses.add(scheduledClass);
        } else {
            findScheduledClassById(scheduledClass.getId()).ifPresent(existing -> {
                int index = scheduledClasses.indexOf(existing);
                scheduledClasses.set(index, scheduledClass);
            });
        }
        return scheduledClass;
    }

    // ATTENDANCE RECORD
    public AttendanceRecord saveAttendanceRecord(AttendanceRecord record) {
        record.setId(attendanceIdCounter.incrementAndGet());
        attendanceRecords.add(record);
        return record;
    }

    public List<AttendanceRecord> findAttendanceByClassId(Long classId) {
        return attendanceRecords.stream()
                .filter(ar -> ar.getScheduledClass().getId().equals(classId))
                .collect(Collectors.toList());
    }

    public boolean hasStudentCheckedIn(Long classId, Long studentId) {
        return attendanceRecords.stream()
                .anyMatch(ar -> ar.getScheduledClass().getId().equals(classId) &&
                        ar.getStudent().getId().equals(studentId));
    }
}