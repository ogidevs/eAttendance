package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.AttendanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final ScheduledClassService scheduledClassService;
    private final UserService userService;

    /**
     * @param classId - id za zakazani cas
     * @return Generiše jedinstveni kod za prisustvo za dati čas i aktivira ga na 15 minuta.
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


    /**
     * @param code - kod za zakazani cas
     * @return boolean
     * Proverava da li je kod validan
     */
    public boolean isCodeValid(String code) {
        Optional<ScheduledClass> scheduledClass = scheduledClassService.findByAttendanceCode(code);
        return scheduledClass.filter(aClass -> LocalDateTime.now().isBefore(aClass.getCodeActiveUntil())).isPresent();

    }

    /**
     * @param studentId - id studenta
     * @return Hvata sve casove koje imaju validan genersani kod i student pripada njima
     */
    public List<ScheduledClass> getAvailableClassesForStudent(Long studentId) {
        List<ScheduledClass> studentClasses = scheduledClassService.findClassesByStudentId(studentId);
        return studentClasses.stream()
                .filter(scheduledClass -> scheduledClass.getAttendanceCode() != null && isCodeValid(scheduledClass.getAttendanceCode())).toList();
    }

    /**
     * @param code - kod za zakazani cas
     * @param studentId - id studenta
     * Evidentira prisustvo studenta koristeći kod.
     * @return Vraća poruku o uspehu ili grešci.
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
     * @param studentId ID studenta.
     * @return Lista zapisa o prisustvu.
     * Vraća kompletnu istoriju prisustva za određenog studenta.
     */
    public List<AttendanceRecord> getAttendanceHistoryForStudent(Long studentId) {
        return attendanceRecordRepository.findByStudentId(studentId);
    }

    /**
     * @param classId ID casa.
     * @return Lista zapisa o prisustvu.
     * Vraća kompletnu istoriju prisustva za određeni cas.
     */
    public List<AttendanceRecord> getAttendanceHistoryForClass(Long classId) {
        return attendanceRecordRepository.findByClassId(classId);
    }
}