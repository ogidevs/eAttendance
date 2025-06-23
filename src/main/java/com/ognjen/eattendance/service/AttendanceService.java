package com.ognjen.eattendance.service;


import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.InMemoryDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final InMemoryDataRepository repository;
    private final UserService userService;

    /**
     * Generiše jedinstveni kod za prisustvo za dati čas i aktivira ga na 15 minuta.
     */
    public String generateAttendanceCodeForClass(Long classId) {
        ScheduledClass scheduledClass = repository.findScheduledClassById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Čas sa ID-jem " + classId + " ne postoji."));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        scheduledClass.setAttendanceCode(code);
        scheduledClass.setCodeActiveUntil(LocalDateTime.now().plusMinutes(15));

        repository.saveScheduledClass(scheduledClass); // Sačuvaj promene
        return code;
    }

    /**
     * Evidentira prisustvo studenta koristeći kod.
     * Vraća poruku o uspehu ili grešci.
     */
    public String recordAttendance(String code, Long studentId) {
        // 1. Proveri da li student postoji
        Student student = userService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student ne postoji."));

        // 2. Pronađi čas sa datim kodom
        ScheduledClass scheduledClass = repository.findScheduledClassByCode(code)
                .orElse(null);

        if (scheduledClass == null) {
            return "Greška: Uneti kod je nevažeći.";
        }

        // 3. Proveri da li je kod istekao
        if (LocalDateTime.now().isAfter(scheduledClass.getCodeActiveUntil())) {
            return "Greška: Vreme za prijavu je isteklo.";
        }

        // 4. Proveri da li je student upisan na predmet
        boolean isEnrolled = scheduledClass.getSubject().getEnrolledStudents().stream()
                .anyMatch(s -> s.getId().equals(studentId));

        if (!isEnrolled) {
            return "Greška: Niste upisani na ovaj predmet.";
        }

        // 5. Proveri da li se student već prijavio
        if (repository.hasStudentCheckedIn(scheduledClass.getId(), studentId)) {
            return "Info: Već ste evidentirani za ovaj čas.";
        }

        // Sve provere su prošle, evidentiraj prisustvo
        AttendanceRecord newRecord = new AttendanceRecord();
        newRecord.setStudent(student);
        newRecord.setScheduledClass(scheduledClass);
        newRecord.setCheckInTime(LocalDateTime.now());

        repository.saveAttendanceRecord(newRecord);

        return "Uspeh: Vaše prisustvo je uspešno evidentirano!";
    }
}