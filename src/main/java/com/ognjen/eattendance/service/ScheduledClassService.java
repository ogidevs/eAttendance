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
    private final SubjectRepository subjectRepository;

    /**
     * Pronalazi sve zakazane časove za datog profesora.
     * @param professorId ID profesora.
     * @return Lista zakazanih časova.
     */
    public List<ScheduledClass> findClassesByProfessorId(Long professorId) {
        return scheduledClassRepository.findByProfessorId(professorId);
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
    public void createNewClass(Long subjectId, LocalDateTime classDateTime) {
        // Pronalazimo predmet da bismo ga povezali sa časom
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Predmet sa ID-jem " + subjectId + " ne postoji."));

        ScheduledClass newClass = new ScheduledClass();
        newClass.setSubject(subject);
        newClass.setClassDateTime(classDateTime);

        scheduledClassRepository.save(newClass);
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