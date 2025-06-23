package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.repository.ProfessorRepository;
import com.ognjen.eattendance.repository.StudentRepository;
import com.ognjen.eattendance.service.SubjectService;
import com.ognjen.eattendance.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SubjectService subjectService;
    // Repozitorijumi su i dalje potrebni za popunjavanje <select> listi u formi za predmete
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    private boolean isNotAdmin(HttpSession session) {
        return !"ADMIN".equals(session.getAttribute("userRole"));
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        return "admin/dashboard";
    }

    // ========== STUDENT CRUD ==========
    @GetMapping("/students")
    public String listStudents(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("students", userService.getAllStudents());
        return "admin/students-list";
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("student", new Student());
        return "admin/student-form";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("student", userService.getStudentById(id).orElseThrow());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        userService.saveStudent(student);
        redirectAttributes.addFlashAttribute("success", "Student je uspešno sačuvan.");
        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        userService.deleteStudentById(id);
        redirectAttributes.addFlashAttribute("success", "Student je uspešno obrisan.");
        return "redirect:/admin/students";
    }

    // ========== PROFESSOR CRUD ==========
    @GetMapping("/professors")
    public String listProfessors(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("professors", userService.getAllProfessors());
        return "admin/professors-list";
    }

    @GetMapping("/professors/add")
    public String showAddProfessorForm(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("professor", new Professor());
        return "admin/professor-form";
    }

    @GetMapping("/professors/edit/{id}")
    public String showEditProfessorForm(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("professor", userService.getProfessorById(id).orElseThrow());
        return "admin/professor-form";
    }

    @PostMapping("/professors/save")
    public String saveProfessor(@ModelAttribute Professor professor, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        userService.saveProfessor(professor);
        redirectAttributes.addFlashAttribute("success", "Profesor je uspešno sačuvan.");
        return "redirect:/admin/professors";
    }

    @PostMapping("/professors/delete/{id}")
    public String deleteProfessor(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        userService.deleteProfessorById(id);
        redirectAttributes.addFlashAttribute("success", "Profesor je uspešno obrisan.");
        return "redirect:/admin/professors";
    }

    // ========== SUBJECT CRUD (ostaje isti) ==========
    @GetMapping("/subjects")
    public String listSubjects(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subjects-list";
    }

    @GetMapping("/subjects/add")
    public String showAddSubjectForm(Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";
        model.addAttribute("subject", new Subject());
        model.addAttribute("allProfessors", professorRepository.findAll());
        model.addAttribute("allStudents", studentRepository.findAll());
        return "admin/subject-form";
    }

    /**
     * Prikazuje formu za izmenu postojećeg predmeta.
     * Šalje iste podatke kao i 'add' metoda, ali sa postojećim objektom.
     */
    @GetMapping("/subjects/edit/{id}")
    public String showEditSubjectForm(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        Subject subject = subjectService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nevalidan ID predmeta:" + id));

        model.addAttribute("subject", subject);
        model.addAttribute("allProfessors", professorRepository.findAll());
        model.addAttribute("allStudents", studentRepository.findAll());
        return "admin/subject-form"; // Vraća istu formu
    }

    /**
     * JEDINSTVENA METODA za čuvanje (i kreiranje i ažuriranje) predmeta.
     */
    @PostMapping("/subjects/save")
    public String saveSubject(@ModelAttribute Subject subject,
                              @RequestParam Long professorId,
                              @RequestParam(required = false) List<Long> studentIds,
                              RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        // 1. Postavljanje veze ka profesoru
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Nevalidan ID profesora:" + professorId));
        subject.setProfessor(professor);

        // 2. Postavljanje veza ka studentima (ceo spisak izabranih)
        if (studentIds != null && !studentIds.isEmpty()) {
            List<Student> enrolledStudents = studentIds.stream()
                    .map(id -> studentRepository.findById(id).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
            subject.setEnrolledStudents(enrolledStudents);
        } else {
            // Ako admin nije izabrao nijednog studenta, brišu se svi sa predmeta
            subject.setEnrolledStudents(Collections.emptyList());
        }

        // 3. Čuvanje predmeta (repozitorijum zna da li je add ili update)
        subjectService.save(subject);
        redirectAttributes.addFlashAttribute("success", "Predmet je uspešno sačuvan.");
        return "redirect:/admin/subjects";
    }

    /**
     * Briše predmet na osnovu ID-ja.
     * @param id ID predmeta za brisanje.
     */
    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isNotAdmin(session)) return "redirect:/login";

        // U realnoj aplikaciji, prvo bi trebalo proveriti da li je ovaj predmet
        // vezan za neke časove (ScheduledClass), da se ne bi narušio integritet.
        // Za ovaj projekat, direktno brisanje je u redu.

        subjectService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Predmet je uspešno obrisan.");
        return "redirect:/admin/subjects";
    }

}