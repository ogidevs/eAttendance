package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.service.AttendanceService;
import com.ognjen.eattendance.service.ScheduledClassService;
import com.ognjen.eattendance.service.SubjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final SubjectService subjectService;
    private final AttendanceService attendanceService;
    private final ScheduledClassService scheduledClassService;

    private boolean isProfessor(HttpSession session) {
        return !"PROFESSOR".equals(session.getAttribute("userRole"));
    }

    // --- READ (Čitanje) ---
    @GetMapping("/dashboard")
    public String professorDashboard(HttpSession session, Model model) {
        if (isProfessor(session)) {
            return "redirect:/login";
        }

        Long professorId = (Long) session.getAttribute("userId");
        if (professorId == null) { return "redirect:/login"; }

        List<Subject> subjects = subjectService.findSubjectsByProfessorId(professorId);
        List<ScheduledClass> classes = scheduledClassService.findClassesByProfessorId(professorId);

        model.addAttribute("subjects", subjects);
        model.addAttribute("classes", classes);
        model.addAttribute("newClass", new ScheduledClass()); // Prazan objekat za formu za dodavanje

        return "professor/dashboard";
    }

    // --- CREATE (Dodavanje novog časa) ---
    @PostMapping("/class/save")
    public String saveClass(@RequestParam Long subjectId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime classDateTime,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        if (isProfessor(session)) {
            return "redirect:/login";
        }
        Long professorId = (Long) session.getAttribute("userId");
        if (professorId == null) { return "redirect:/login"; }

        // BEZBEDNOSNA PROVERA: Da li profesor pokušava da doda čas za svoj predmet?
        boolean subjectBelongsToProfessor = subjectService.findSubjectsByProfessorId(professorId)
                .stream()
                .anyMatch(s -> s.getId().equals(subjectId));

        if (!subjectBelongsToProfessor) {
            redirectAttributes.addFlashAttribute("error", "Ne možete dodati čas za predmet koji ne predajete!");
            return "redirect:/professor/dashboard";
        }

        try {
            scheduledClassService.createNewClass(subjectId, classDateTime);
            redirectAttributes.addFlashAttribute("success", "Novi čas je uspešno zakazan!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Greška pri čuvanju časa: " + e.getMessage());
        }

        return "redirect:/professor/dashboard";
    }

    // --- DELETE (Brisanje časa) ---
    @PostMapping("/class/delete/{classId}")
    public String deleteClass(@PathVariable Long classId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (isProfessor(session)) {
            return "redirect:/login";
        }
        Long professorId = (Long) session.getAttribute("userId");
        if (professorId == null) { return "redirect:/login"; }

        // Prvo proveravamo da li čas uopšte pripada profesoru
        boolean classBelongsToProfessor = scheduledClassService.findClassesByProfessorId(professorId)
                .stream()
                .anyMatch(c -> c.getId().equals(classId));

        if (!classBelongsToProfessor) {
            redirectAttributes.addFlashAttribute("error", "Pokušavate da obrišete čas koji Vam ne pripada!");
            return "redirect:/professor/dashboard";
        }

        // Ako pripada, brišemo ga
        try {
            // Pre brisanja, mogli bismo dodati logiku da se ne može obrisati čas koji ima aktiviran kod ili je u toku
            scheduledClassService.deleteClass(classId); // Pozivamo metodu iz servisa
            redirectAttributes.addFlashAttribute("success", "Čas je uspešno obrisan.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Greška pri brisanju časa: " + e.getMessage());
        }

        return "redirect:/professor/dashboard";
    }


    @PostMapping("/class/{classId}/generate-code")
    public String generateCode(@PathVariable Long classId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (isProfessor(session)) {
            return "redirect:/login";
        }
        Long professorId = (Long) session.getAttribute("userId");
        if (professorId == null) { return "redirect:/login"; }

        // ISPRAVKA: Koristimo pravi servis za bezbednosnu proveru
        boolean classBelongsToProfessor = scheduledClassService.findClassesByProfessorId(professorId)
                .stream()
                .anyMatch(c -> c.getId().equals(classId));

        if (!classBelongsToProfessor) {
            redirectAttributes.addFlashAttribute("error", "Pokušavate da generišete kod za čas koji Vam ne pripada!");
            return "redirect:/professor/dashboard";
        }

        try {
            String code = attendanceService.generateAttendanceCodeForClass(classId);
            redirectAttributes.addFlashAttribute("success", "Kod za prisustvo je generisan: " + code);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Greška prilikom generisanja koda: " + e.getMessage());
        }

        return "redirect:/professor/dashboard";
    }
}