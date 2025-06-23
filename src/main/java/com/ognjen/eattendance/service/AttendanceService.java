package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.AttendanceRecordRepository;
import com.ognjen.eattendance.repository.ScheduledClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final ScheduledClassRepository scheduledClassRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserService userService;

    /**
     * Generiše jedinstveni kod za prisustvo za dati čas i aktivira ga na 15 minuta.
     */
    public String generateAttendanceCodeForClass(Long classId) {
        // Logika ostaje ista...
        ScheduledClass scheduledClass = scheduledClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Čas sa ID-jem " + classId + " ne postoji."));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        scheduledClass.setAttendanceCode(code);
        scheduledClass.setCodeActiveUntil(LocalDateTime.now().plusMinutes(15));

        scheduledClassRepository.save(scheduledClass);
        return code;
    }

    /**
     * Evidentira prisustvo studenta koristeći kod.
     * Vraća poruku o uspehu ili grešci.
     */
    public String recordAttendance(String code, Long studentId) {
        // Logika ostaje ista...
        Student student = userService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student ne postoji."));

        ScheduledClass scheduledClass = scheduledClassRepository.findByAttendanceCode(code)
                .orElse(null);

        if (scheduledClass == null) {
            return "Greška: Uneti kod je nevažeći.";
        }

        // ... ostatak logike ostaje nepromenjen ...

        if (LocalDateTime.now().isAfter(scheduledClass.getCodeActiveUntil())) {
            return "Greška: Vreme za prijavu je isteklo.";
        }

        boolean isEnrolled = scheduledClass.getSubject().getEnrolledStudents().stream()
                .anyMatch(s -> s.getId().equals(studentId));

        if (!isEnrolled) {
            return "Greška: Niste upisani na ovaj predmet.";
        }

        if (attendanceRecordRepository.existsByClassIdAndStudentId(scheduledClass.getId(), studentId)) {
            return "Info: Već ste evidentirani za ovaj čas.";
        }

        AttendanceRecord newRecord = new AttendanceRecord();
        newRecord.setStudent(student);
        newRecord.setScheduledClass(scheduledClass);
        newRecord.setCheckInTime(LocalDateTime.now());

        attendanceRecordRepository.save(newRecord);

        return "Uspeh: Vaše prisustvo je uspešno evidentirano!";
    }
}