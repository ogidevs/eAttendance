package com.ognjen.eattendance.controller;
import com.ognjen.eattendance.service.AttendanceService;
import com.ognjen.eattendance.service.SubjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private SubjectService subjectService; // Potreban jer je deo konstruktora

    @Test
    @DisplayName("Putanja 1 (Happy Path): Student je ulogovan i uspešno evidentira prisustvo")
    void checkIn_Success_WhenLoggedInAndAllConditionsMet() throws Exception {
        // Arrange
        String validCode = "A1B2C3";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", "STUDENT");
        session.setAttribute("userId", 1L);

        when(attendanceService.recordAttendance(validCode, 1L))
                .thenReturn("Uspeh: Vaše prisustvo je uspešno evidentirano!");

        // Act & Assert
        mockMvc.perform(get("/student/check-in/{code}", validCode).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(flash().attribute("success", "Uspeh: Vaše prisustvo je uspešno evidentirano!"));
    }

    @Test
    @DisplayName("Putanja 2 (Potreba za prijavom): Student nije ulogovan, preusmerava na login")
    void checkIn_RedirectsToLogin_WhenNotLoggedIn() throws Exception {
        // Arrange
        String code = "A1B2C3";
        String expectedRedirectAfterLogin = "/student/check-in/" + code;

        // Act & Assert
        mockMvc.perform(get("/student/check-in/{code}", code)) // Nema sesije
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(request().sessionAttribute("redirectAfterLogin", expectedRedirectAfterLogin));
    }

    @Test
    @DisplayName("Putanja 3, 4, 5 (Greška iz servisa): Ulogovan student, ali servis vraća grešku")
    void checkIn_RedirectsWithError_WhenServiceThrowsException() throws Exception {
        // Arrange (Ovaj test pokriva sve negativne scenarije nakon što je student ulogovan)
        String expiredCode = "EXPIRED";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", "STUDENT");
        session.setAttribute("userId", 1L);

        // Npr. sesija istekla, nije autorizovan, već prijavljen -> svi bacaju izuzetak
        when(attendanceService.recordAttendance(expiredCode, 1L))
                .thenThrow(new IllegalArgumentException("Greška Vreme za prijavu je isteklo."));

        // Act & Assert
        mockMvc.perform(get("/student/check-in/{code}", expiredCode).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Greška pri brisanju časa: Greška Vreme za prijavu je isteklo."));
    }
}