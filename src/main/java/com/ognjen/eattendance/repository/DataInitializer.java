package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    // Dependency Injection: Spring će automatski ubaciti instance svih potrebnih repozitorijuma.
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;
    private final ScheduledClassRepository scheduledClassRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    /**
     * @PostConstruct - Anotacija koja osigurava da će ova metoda biti izvršena
     *                  JEDNOM, odmah nakon što je Spring kreirao 'DataInitializer' bean
     *                  i ubacio sve njegove zavisnosti.
     */
    @PostConstruct
    public void init() {
        // --- 1. Kreiranje profesora ---
        Professor p1 = new Professor();
        p1.setUsername("prof.petrovic");
        p1.setPassword("pass"); // U realnoj aplikaciji, lozinke bi bile heširane
        p1.setFirstName("Petar");
        p1.setLastName("Petrović");
        p1.setTitle("Dr");
        professorRepository.save(p1); // Čuvanje u repo da bi dobio ID

        Professor p2 = new Professor();
        p2.setUsername("prof.jovanovic");
        p2.setPassword("pass");
        p2.setFirstName("Jovana");
        p2.setLastName("Jovanović");
        p2.setTitle("Asistent");
        professorRepository.save(p2);

        // --- 2. Kreiranje studenata ---
        Student s1 = new Student();
        s1.setUsername("stud.markovic");
        s1.setPassword("pass");
        s1.setFirstName("Marko");
        s1.setLastName("Marković");
        s1.setIndexNumber("IT 15/2020");
        studentRepository.save(s1); // Čuvanje u repo da bi dobio ID

        Student s2 = new Student();
        s2.setUsername("stud.ilic");
        s2.setPassword("pass");
        s2.setFirstName("Ilija");
        s2.setLastName("Ilić");
        s2.setIndexNumber("IT 22/2020");
        studentRepository.save(s2);

        Student s3 = new Student();
        s3.setUsername("stud.simic");
        s3.setPassword("pass");
        s3.setFirstName("Ana");
        s3.setLastName("Simić");
        s3.setIndexNumber("IT 31/2021");
        studentRepository.save(s3);

        // --- 3. Kreiranje predmeta i povezivanje ---
        // Redosled je važan: prvo moraju postojati profesori i studenti
        Subject sub1 = new Subject();
        sub1.setName("Web Sistemi 1");
        sub1.setCode("IT355");
        sub1.setProfessor(p1); // Povezivanje sa već sačuvanim profesorom
        sub1.setEnrolledStudents(List.of(s1, s2, s3)); // Povezivanje sa studentima
        subjectRepository.save(sub1);

        Subject sub2 = new Subject();
        sub2.setName("Baze Podataka");
        sub2.setCode("CS323");
        sub2.setProfessor(p2);
        sub2.setEnrolledStudents(List.of(s1, s3)); // s2 ne sluša ovaj predmet
        subjectRepository.save(sub2);

        // --- 4. Kreiranje zakazanih časova ---
        ScheduledClass sc1 = new ScheduledClass();
        sc1.setSubject(sub1); // Čas za Web Sisteme
        sc1.setClassDateTime(LocalDateTime.now().minusDays(1).withHour(10).withMinute(15)); // Juče u 10:15
        scheduledClassRepository.save(sc1);

        ScheduledClass sc2 = new ScheduledClass();
        sc2.setSubject(sub1);
        sc2.setClassDateTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(15)); // Sutra u 12:15
        scheduledClassRepository.save(sc2);

        ScheduledClass sc3 = new ScheduledClass();
        sc3.setSubject(sub2); // Čas za Baze Podataka
        sc3.setClassDateTime(LocalDateTime.now().minusDays(2).withHour(14).withMinute(0)); // Pre dva dana u 14:00
        scheduledClassRepository.save(sc3);

        // --- 5. Kreiranje istorije prisustva (opciono, ali korisno za testiranje) ---
        // Evidentiramo da je Marko bio prisutan na času koji je održan juče
        AttendanceRecord ar1 = new AttendanceRecord();
        ar1.setStudent(s1);
        ar1.setScheduledClass(sc1);
        ar1.setCheckInTime(sc1.getClassDateTime().plusMinutes(5)); // Prijavio se 5 min nakon početka
        attendanceRecordRepository.save(ar1);

        // Evidentiramo da je Ana takođe bila prisutna
        AttendanceRecord ar2 = new AttendanceRecord();
        ar2.setStudent(s3);
        ar2.setScheduledClass(sc1);
        ar2.setCheckInTime(sc1.getClassDateTime().plusMinutes(2));
        attendanceRecordRepository.save(ar2);


        // Ispis u konzolu da potvrdimo da je inicijalizacija prošla
        System.out.println("=====================================================");
        System.out.println("Data Initializer: In-memory database populated with test data.");
        System.out.println("=====================================================");
    }
}