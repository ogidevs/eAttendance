package com.ognjen.eattendance.controller;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.ProfessorRepository;
import com.ognjen.eattendance.repository.StudentRepository;
import com.ognjen.eattendance.service.SubjectService;
import com.ognjen.eattendance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    // Mock-ujemo i ostale zavisnosti koje AdminController koristi, čak i ako nisu direktno u testu
    @MockitoBean
    private SubjectService subjectService;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private ProfessorRepository professorRepository;

    private MockHttpSession adminSession;

    @BeforeEach
    void setUp() {
        adminSession = new MockHttpSession();
        adminSession.setAttribute("userRole", "ADMIN");
    }

    @Test
    @DisplayName("TC-ADM-CRUD-01: Admin uspešno čuva (kreira) studenta")
    void saveStudent_ShouldRedirectWithSuccessMessage() throws Exception {
        mockMvc.perform(post("/admin/students/save")
                        .session(adminSession)
                        .param("username", "novi.student")
                        .param("firstName", "Novi")
                        .param("lastName", "Student"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students"))
                .andExpect(flash().attribute("success", "Student je uspešno sačuvan."));

        // Verifikujemo da je metoda zaista pozvana
        verify(userService).saveStudent(any(Student.class));
    }

    @Test
    @DisplayName("TC-ADM-CRUD-01: Admin uspešno briše studenta")
    void deleteStudent_ShouldRedirectWithSuccessMessage() throws Exception {
        Long studentIdToDelete = 1L;

        mockMvc.perform(post("/admin/students/delete/{id}", studentIdToDelete)
                        .session(adminSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students"))
                .andExpect(flash().attribute("success", "Student je uspešno obrisan."));

        // Verifikujemo da je metoda za brisanje pozvana sa pravim ID-jem
        verify(userService).deleteStudentById(studentIdToDelete);
    }
}