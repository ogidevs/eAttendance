package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.AttendanceRecordRepository;
import com.ognjen.eattendance.repository.ScheduledClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final ScheduledClassService scheduledClassService;
    private final UserService userService;

    /**
     * Generiše jedinstveni kod za prisustvo za dati čas i aktivira ga na 15 minuta.
     */
    public String generateAttendanceCodeForClass(Long classId) {
        // Logika ostaje ista...
        ScheduledClass scheduledClass = scheduledClassService.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Čas sa ID-jem " + classId + " ne postoji."));

        if (LocalDateTime.now().isBefore(scheduledClass.getClassDateTime())) {
            throw new IllegalArgumentException("Greška: Ne možete da generišete kod pre početka časa");
        }

        if (LocalDateTime.now().isAfter(scheduledClass.getClassDateTime().plusMinutes(scheduledClass.getDuration()))) {
            throw new IllegalArgumentException("Greška: Ne možete da generišete za završeni čas");

        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        scheduledClass.setAttendanceCode(code);
        scheduledClass.setCodeActiveUntil(LocalDateTime.now().plusMinutes(15));

        scheduledClassService.save(scheduledClass);
        return code;
    }

    public boolean isCodeValid(String code) {
        Optional<ScheduledClass> scheduledClass = scheduledClassService.findByAttendanceCode(code);
        return scheduledClass.filter(aClass -> LocalDateTime.now().isBefore(aClass.getCodeActiveUntil())).isPresent();

    }

    public List<ScheduledClass> getAvailableClassesForStudent(Long studentId) {
        List<ScheduledClass> studentClasses = scheduledClassService.findClassesByStudentId(studentId);
        return studentClasses.stream()
                .filter(scheduledClass -> scheduledClass.getAttendanceCode() != null && isCodeValid(scheduledClass.getAttendanceCode())).toList();
    }

    /**
     * Evidentira prisustvo studenta koristeći kod.
     * Vraća poruku o uspehu ili grešci.
     */
    public String recordAttendance(String code, Long studentId) {
        // Logika ostaje ista...
        Student student = userService.getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student ne postoji."));

        ScheduledClass scheduledClass = scheduledClassService.findByAttendanceCode(code)
                .orElse(null);

        if (scheduledClass == null) {
            return "Greška: Uneti kod je nevažeći.";
        }

        // ... ostatak logike ostaje nepromenjen ...

        if (LocalDateTime.now().isAfter(scheduledClass.getCodeActiveUntil())) {
            return "Greška: Vreme za prijavu je isteklo.";
        }

        if (LocalDateTime.now().isBefore(scheduledClass.getClassDateTime())) {
            return "Greška: Čas još uvek nije počeo";
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

    /**
     * Vraća kompletnu istoriju prisustva za određenog studenta.
     * @param studentId ID studenta.
     * @return Lista zapisa o prisustvu.
     */
    public List<AttendanceRecord> getAttendanceHistoryForStudent(Long studentId) {
        return attendanceRecordRepository.findByStudentId(studentId);
    }

    public List<AttendanceRecord> getAttendanceHistoryForClass(Long classId) {
        return attendanceRecordRepository.findByClassId(classId);
    }
}