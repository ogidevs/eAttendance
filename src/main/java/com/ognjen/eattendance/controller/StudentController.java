package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final AttendanceService attendanceService;

    // Provera da li je korisnik ulogovan kao student
    private boolean isStudent(HttpSession session) {
        return !"STUDENT".equals(session.getAttribute("userRole"));
    }

    @GetMapping("/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        if (isStudent(session)) {
            return "redirect:/login";
        }
        // Ovde se može dodati logika za prikaz istorije prisustva
        return "student/dashboard"; // Vraća student/dashboard.html
    }

    @PostMapping("/check-in")
    public String checkIn(@RequestParam String attendanceCode,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (isStudent(session)) {
            return "redirect:/login";
        }

        Long studentId = (Long) session.getAttribute("userId");
        if (studentId == null) {
            redirectAttributes.addFlashAttribute("error", "Sesija je istekla, molimo prijavite se ponovo.");
            return "redirect:/login";
        }

        // Pozivamo servis koji sadrži svu logiku
        String resultMessage = attendanceService.recordAttendance(attendanceCode.trim().toUpperCase(), studentId);

        // Koristimo RedirectAttributes da pošaljemo poruku nazad na dashboard
        if (resultMessage.startsWith("Uspeh")) {
            redirectAttributes.addFlashAttribute("success", resultMessage);
        } else {
            redirectAttributes.addFlashAttribute("error", resultMessage);
        }

        return "redirect:/student/dashboard";
    }
}