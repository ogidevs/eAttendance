package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Vraća login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // 1. Provera za hardkodovanog Administratora
        if ("admin".equals(username) && "adminpass".equals(password)) {
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("username", "Administrator");
            return "redirect:/admin/dashboard";
        }

        // 2. Provera za Profesore
        Optional<Professor> professorOpt = userService.getProfessorByUsername(username);
        if (professorOpt.isPresent() && professorOpt.get().getPassword().equals(password)) {
            Professor professor = professorOpt.get();
            session.setAttribute("userRole", "PROFESSOR");
            session.setAttribute("userId", professor.getId());
            session.setAttribute("username", professor.getFirstName() + " " + professor.getLastName());
            return "redirect:/professor/dashboard";
        }

        // 3. Provera za Studente
        Optional<Student> studentOpt = userService.getStudentByUsername(username);
        if (studentOpt.isPresent() && studentOpt.get().getPassword().equals(password)) {
            Student student = studentOpt.get();
            session.setAttribute("userRole", "STUDENT");
            session.setAttribute("userId", student.getId());
            session.setAttribute("username", student.getFirstName() + " " + student.getLastName());
            return "redirect:/student/dashboard";
        }

        // Ako niko nije pronađen
        redirectAttributes.addFlashAttribute("error", "Pogrešno korisničko ime ili lozinka!");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Poništava sesiju
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        // 1. Proveri da li uloga postoji u sesiji
        Object roleObject = session.getAttribute("userRole");

        // 2. Ako ne postoji, korisnik nije ulogovan -> idi na login
        if (roleObject == null) {
            return "redirect:/login";
        }

        String userRole = (String) roleObject;

        // 3. U suprotnom, preusmeri na dashboard prema ulozi
        switch (userRole) {
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "PROFESSOR":
                return "redirect:/professor/dashboard";
            case "STUDENT":
                return "redirect:/student/dashboard";
            default:
                // Ako se desi neka neočekivana uloga, siguran povratak na login
                return "redirect:/login";
        }
    }
}