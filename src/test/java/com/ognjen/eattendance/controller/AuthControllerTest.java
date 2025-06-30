package com.ognjen.eattendance.controller;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("TC-LOGIN-01: Uspešna prijava administratora")
    void processLogin_ShouldRedirectToAdminDashboard_ForAdminCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "adminpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(request().sessionAttribute("userRole", "ADMIN"));
    }

    @Test
    @DisplayName("TC-LOGIN-02: Neuspešna prijava sa pogrešnom lozinkom")
    void processLogin_ShouldRedirectToLoginWithError_ForInvalidCredentials() throws Exception {
        // Simuliramo da korisnik ne postoji ni u jednoj tabeli
        when(userService.getProfessorByUsername("admin")).thenReturn(Optional.empty());
        when(userService.getStudentByUsername("admin")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "pogresnalozinka"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("error", "Pogrešno korisničko ime ili lozinka!"));
    }

    @Test
    @DisplayName("Uspešna prijava studenta i preusmeravanje na dashboard")
    void processLogin_ShouldRedirectToStudentDashboard_ForStudentCredentials() throws Exception {
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setUsername("stud.markovic");
        mockStudent.setPassword("pass");
        mockStudent.setFirstName("Marko");
        mockStudent.setLastName("Marković");

        when(userService.getStudentByUsername("stud.markovic")).thenReturn(Optional.of(mockStudent));

        mockMvc.perform(post("/login")
                        .param("username", "stud.markovic")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(request().sessionAttribute("userRole", "STUDENT"))
                .andExpect(request().sessionAttribute("userId", 1L));
    }
}