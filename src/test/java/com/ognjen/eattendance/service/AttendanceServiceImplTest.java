package com.ognjen.eattendance.service;
import com.ognjen.eattendance.model.*;
import com.ognjen.eattendance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceServiceImplTest {

    private AttendanceService attendanceService;
    private ScheduledClassService scheduledClassService;
    private UserService userService;
    private SubjectService subjectService;

    private AttendanceRecordRepository attendanceRecordRepository;
    private ScheduledClassRepository scheduledClassRepository;
    private StudentRepository studentRepository;
    private SubjectRepository subjectRepository;

    // Test podaci
    private Student enrolledStudent;
    private Student notEnrolledStudent;
    private Professor professor;
    private Subject subject;
    private ScheduledClass scheduledClass;
    private ScheduledClass futureClass;
    private ScheduledClass pastFinishedClass;


    @BeforeEach
    void setUp() {
        // Inicijalizacija In-Memory repozitorijuma za svaki test
        studentRepository = new StudentRepository();
        ProfessorRepository professorRepository = new ProfessorRepository();
        subjectRepository = new SubjectRepository();
        scheduledClassRepository = new ScheduledClassRepository();
        attendanceRecordRepository = new AttendanceRecordRepository();

        // Inicijalizacija servisa sa repozitorijumima
        userService = new UserServiceImpl(studentRepository, professorRepository, subjectService);
        subjectService = new SubjectServiceImpl(subjectRepository);
        scheduledClassService = new ScheduledClassServiceImpl(scheduledClassRepository, subjectService);
        attendanceService = new AttendanceServiceImpl(attendanceRecordRepository, scheduledClassService, userService);

        // Kreiranje test podataka
        professor = new Professor();
        professor.setUsername("prof.test");
        professor.setPassword("pass");
        professorRepository.save(professor);

        enrolledStudent = new Student();
        enrolledStudent.setUsername("student.upisan");
        enrolledStudent.setPassword("pass");
        studentRepository.save(enrolledStudent);

        notEnrolledStudent = new Student();
        notEnrolledStudent.setUsername("student.neupisan");
        notEnrolledStudent.setPassword("pass");
        studentRepository.save(notEnrolledStudent);

        subject = new Subject();
        subject.setName("Test Predmet");
        subject.setProfessor(professor);
        subject.setEnrolledStudents(List.of(enrolledStudent)); // Samo je jedan student upisan
        subjectRepository.save(subject);

        // Cas koji je poceo i za koji se moze generisati kod
        scheduledClass = new ScheduledClass();
        scheduledClass.setSubject(subject);
        scheduledClass.setClassDateTime(LocalDateTime.now().minusMinutes(10)); // Počeo pre 10 min
        scheduledClass.setDuration(90);
        scheduledClassRepository.save(scheduledClass);

        // Cas koji jos nije poceo
        futureClass = new ScheduledClass();
        futureClass.setSubject(subject);
        futureClass.setClassDateTime(LocalDateTime.now().plusHours(1)); // Počinje za 1h
        futureClass.setDuration(90);
        scheduledClassRepository.save(futureClass);

        // Cas koji je zavrsen
        pastFinishedClass = new ScheduledClass();
        pastFinishedClass.setSubject(subject);
        pastFinishedClass.setClassDateTime(LocalDateTime.now().minusHours(3)); // Počeo pre 3h
        pastFinishedClass.setDuration(90); // Trajao 90 min, dakle završen je
        scheduledClassRepository.save(pastFinishedClass);
    }

    // --- Testovi za recordAttendance() metodu ---
    // Odgovara "Happy Path" / "Test Slučaj A"

    @Test
    @DisplayName("Putanja 1: Uspešna evidencija prisustva (Happy Path)")
    void recordAttendance_Success() {
        // Arrange: Generiši validan, aktivan kod
        String code = attendanceService.generateAttendanceCodeForClass(scheduledClass.getId());

        // Act: Student pokušava da evidentira prisustvo
        String result = attendanceService.recordAttendance(code, enrolledStudent.getId());

        // Assert: Proveri poruku o uspehu i da li je zapis kreiran
        assertEquals("Uspeh: Vaše prisustvo je uspešno evidentirano!", result);
        assertTrue(attendanceRecordRepository.existsByClassIdAndStudentId(scheduledClass.getId(), enrolledStudent.getId()));
    }

    @Test
    @DisplayName("Putanja 3: Neuspešna evidencija - Sesija istekla")
    void recordAttendance_Failure_WhenSessionIsExpired() {
        // Arrange: Generiši kod, ali ga manuelno postavi da je istekao
        String code = attendanceService.generateAttendanceCodeForClass(scheduledClass.getId());
        ScheduledClass sc = scheduledClassRepository.findByAttendanceCode(code).get();
        sc.setCodeActiveUntil(LocalDateTime.now().minusSeconds(1)); // Vreme je isteklo pre 1 sekunde
        scheduledClassRepository.save(sc);

        // Act & Assert: Očekuj izuzetak sa odgovarajućom porukom
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.recordAttendance(code, enrolledStudent.getId());
        });
        assertEquals("Greška Vreme za prijavu je isteklo.", exception.getMessage());
    }

    @Test
    @DisplayName("Putanja 4: Neuspešna evidencija - Student nije upisan na predmet")
    void recordAttendance_Failure_WhenStudentNotEnrolled() {
        // Arrange: Generiši validan kod
        String code = attendanceService.generateAttendanceCodeForClass(scheduledClass.getId());

        // Act & Assert: Student koji nije upisan pokušava da se prijavi
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.recordAttendance(code, notEnrolledStudent.getId());
        });
        assertEquals("Greška Niste upisani na ovaj predmet.", exception.getMessage());
    }

    @Test
    @DisplayName("Putanja 5: Neuspešna evidencija - Student već evidentiran")
    void recordAttendance_Failure_WhenAlreadyRecorded() {
        // Arrange: Generiši kod i uspešno evidentiraj studenta jednom
        String code = attendanceService.generateAttendanceCodeForClass(scheduledClass.getId());
        attendanceService.recordAttendance(code, enrolledStudent.getId());

        // Act: Pokušaj ponovne evidencije
        String result = attendanceService.recordAttendance(code, enrolledStudent.getId());

        // Assert: Proveri da li je vraćena informativna poruka
        assertEquals("Info: Već ste evidentirani za ovaj čas.", result);
    }

    @Test
    @DisplayName("Dodatna putanja: Neuspešna evidencija - Kod ne postoji")
    void recordAttendance_Failure_WhenCodeIsInvalid() {
        // Act & Assert: Pokušaj evidencije sa nepostojećim kodom
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.recordAttendance("INVALID", enrolledStudent.getId());
        });
        assertEquals("Greška Uneti kod je nevažeći.", exception.getMessage());
    }

    @Test
    @DisplayName("Dodatna putanja: Neuspešna evidencija - Čas još nije počeo")
    void recordAttendance_Failure_WhenClassHasNotStarted() {
        // Arrange: Ručno kreiramo čas u budućnosti sa aktivnim kodom za potrebe testa
        futureClass.setAttendanceCode("FUTURE1");
        futureClass.setCodeActiveUntil(LocalDateTime.now().plusMinutes(15));
        scheduledClassRepository.save(futureClass);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.recordAttendance("FUTURE1", enrolledStudent.getId());
        });
        assertEquals("Greška Čas još uvek nije počeo", exception.getMessage());
    }


    // --- Testovi za generateAttendanceCodeForClass() metodu ---

    @Test
    @DisplayName("Generisanje koda - Uspeh za čas koji je u toku")
    void generateAttendanceCode_Success_ForOngoingClass() {
        // Act
        String code = attendanceService.generateAttendanceCodeForClass(scheduledClass.getId());

        // Assert
        assertNotNull(code);
        assertEquals(6, code.length());

        Optional<ScheduledClass> updatedClassOpt = scheduledClassRepository.findById(scheduledClass.getId());
        assertTrue(updatedClassOpt.isPresent());
        ScheduledClass updatedClass = updatedClassOpt.get();
        assertEquals(code, updatedClass.getAttendanceCode());
        assertTrue(updatedClass.getCodeActiveUntil().isAfter(LocalDateTime.now().plusMinutes(14)));
    }

    @Test
    @DisplayName("Generisanje koda - Neuspeh za čas koji još nije počeo")
    void generateAttendanceCode_Failure_ForFutureClass() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.generateAttendanceCodeForClass(futureClass.getId());
        });
        assertEquals("Greška: Ne možete da generišete kod pre početka časa", exception.getMessage());
    }

    @Test
    @DisplayName("Generisanje koda - Neuspeh za čas koji je završen")
    void generateAttendanceCode_Failure_ForFinishedClass() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceService.generateAttendanceCodeForClass(pastFinishedClass.getId());
        });
        assertEquals("Greška: Ne možete da generišete za završeni čas", exception.getMessage());
    }

}