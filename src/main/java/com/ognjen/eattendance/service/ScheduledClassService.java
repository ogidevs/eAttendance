package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.repository.ScheduledClassRepository;
import com.ognjen.eattendance.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduledClassService {

    private final ScheduledClassRepository scheduledClassRepository;
    private final SubjectService subjectService;

    public void save(ScheduledClass sc) {
        scheduledClassRepository.save(sc);
    }

    /**
     * Pronalazi sve zakazane časove za datog profesora.
     * @param professorId ID profesora.
     * @return Lista zakazanih časova.
     */
    public List<ScheduledClass> findClassesByProfessorId(Long professorId) {
        return scheduledClassRepository.findByProfessorId(professorId);
    }


    /**
     * Pronalazi sve zakazane časove za datog studenta.
     * @param studentID ID profesora.
     * @return Lista zakazanih časova.
     */
    public List<ScheduledClass> findClassesByStudentId(Long studentID) {
        return scheduledClassRepository.findClassesByStudentId(studentID);
    }

    /**
     * Pronalazi zakazani čas po njegovom ID-ju.
     * @param classId ID časa.
     * @return Optional koji sadrži čas ako postoji.
     */
    public Optional<ScheduledClass> findById(Long classId) {
        return scheduledClassRepository.findById(classId);
    }

    /**
     * Kreira novi zakazani čas za određeni predmet i vreme.
     * @param subjectId ID predmeta za koji se čas zakazuje.
     * @param classDateTime Datum i vreme održavanja časa.
     */
    public void createNewClass(Long subjectId, LocalDateTime classDateTime, Integer duration) {
        // Pronalazimo predmet da bismo ga povezali sa časom
        Subject subject = subjectService.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Predmet sa ID-jem " + subjectId + " ne postoji."));

        if (duration <= 0) {
            throw new IllegalArgumentException("Trajanje casa mora biti pozitivna vrednost");
        }
        ScheduledClass newClass = new ScheduledClass();
        newClass.setSubject(subject);
        newClass.setClassDateTime(classDateTime);
        newClass.setDuration(duration);

        scheduledClassRepository.save(newClass);
    }

    /**
     * Traži sve časove po kodu
     * @param code kod za prisustvo.
     */
    public Optional<ScheduledClass> findByAttendanceCode(String code) {
        return scheduledClassRepository.findByAttendanceCode(code);
    }

    /**
     * Briše zakazani čas.
     * @param classId ID časa koji se briše.
     */
    public void deleteClass(Long classId) {
        // U realnoj aplikaciji, prvo bismo obrisali sve vezane 'AttendanceRecord'
        // Za sada, samo brišemo čas. (Pretpostavimo da ScheduledClassRepository ima deleteById metodu)
        // scheduledClassRepository.deleteById(classId);
        System.out.println("Logika za brisanje časa sa ID: " + classId);
    }
}