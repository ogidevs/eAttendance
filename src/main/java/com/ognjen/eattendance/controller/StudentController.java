package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final AttendanceService attendanceService;

    // Provera da li je korisnik ulogovan kao student
    private boolean isNotstudent(HttpSession session) {
        return !"STUDENT".equals(session.getAttribute("userRole"));
    }

    @GetMapping("/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        if (isNotstudent(session)) {
            return "redirect:/login";
        }
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId == null) {
            return "redirect:/login"; // Sigurnosna provera
        }

        List<AttendanceRecord> attendanceHistory = attendanceService.getAttendanceHistoryForStudent(studentId);
        model.addAttribute("history", attendanceHistory);
        List<ScheduledClass> availableClasses = attendanceService.getAvailableClassesForStudent(studentId);
        model.addAttribute("availableClasses", availableClasses);

        return "student/dashboard";
    }

    @GetMapping("/check-in/{code}")
    public String checkIn(@PathVariable("code") String code, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotstudent(session)) {
            session.setAttribute("redirectAfterLogin", "/student/check-in/" + code);
            return "redirect:/login";
        }
        Long studentId = (Long) session.getAttribute("userId");
        if (studentId == null) {
            return "redirect:/login";
        }


        try {
            String resultMessage = attendanceService.recordAttendance(code.trim().toUpperCase(), studentId);
            redirectAttributes.addFlashAttribute("success", resultMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Greška pri brisanju časa: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }
}