package com.ognjen.eattendance.controller;

import com.ognjen.eattendance.model.AttendanceRecord;
import com.ognjen.eattendance.model.ScheduledClass;
import com.ognjen.eattendance.model.Subject;
import com.ognjen.eattendance.service.AttendanceService;
import com.ognjen.eattendance.service.ScheduledClassService;
import com.ognjen.eattendance.service.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfessorController.class)
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubjectService subjectService;
    @MockitoBean
    private AttendanceService attendanceService;
    @MockitoBean
    private ScheduledClassService scheduledClassService;

    private MockHttpSession professorSession;

    @BeforeEach
    void setUp() {
        professorSession = new MockHttpSession();
        professorSession.setAttribute("userRole", "PROFESSOR");
        professorSession.setAttribute("userId", 1L);
    }

    @Test
    @DisplayName("TC-PROF-CLASS-01: Profesor uspešno zakazuje novi čas")
    void saveClass_ShouldRedirectWithSuccessMessage() throws Exception {
        Subject mockSubject = new Subject();
        mockSubject.setId(1L);
        when(subjectService.findSubjectsByProfessorId(1L)).thenReturn(List.of(mockSubject));

        // Act & Assert
        mockMvc.perform(post("/professor/class/save")
                        .session(professorSession)
                        .param("subjectId", "1")
                        .param("classDateTime", "2025-01-01T10:00:00")
                        .param("classDuration", "90"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/dashboard"))
                // ISPRAVKA: Proveravamo flash atribut, ne model atribut
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Novi čas je uspešno zakazan!"));

        verify(scheduledClassService).createNewClass(anyLong(), any(LocalDateTime.class), any(Integer.class));
    }

    @Test
    @DisplayName("TC-INT-01: Uspešno generisanje koda za prisustvo od strane profesora")
    void generateCode_Success_WhenProfessorOwnsTheClass() throws Exception {
        // Arrange
        Long professorId = 1L;
        Long classId = 101L;
        String generatedCode = "XYZ123";

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", "PROFESSOR");
        session.setAttribute("userId", professorId);

        // Bezbednosna provera: Simuliramo da čas pripada ulogovanom profesoru
        ScheduledClass mockClass = new ScheduledClass();
        mockClass.setId(classId);
        when(scheduledClassService.findClassesByProfessorId(professorId)).thenReturn(List.of(mockClass));

        // Simuliramo uspešno generisanje koda u servisu
        when(attendanceService.generateAttendanceCodeForClass(classId)).thenReturn(generatedCode);

        // Act & Assert
        mockMvc.perform(post("/professor/class/{classId}/generate-code", classId).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/dashboard"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Kod za prisustvo je generisan: " + generatedCode + " | Kod traje narednih 15 min."));
    }

    @Test
    @DisplayName("TC-INT-01 (Negativni scenario): Neuspešno generisanje koda - profesor ne predaje na času")
    void generateCode_Failure_WhenProfessorDoesNotOwnTheClass() throws Exception {
        // Arrange
        Long professorId = 1L;
        Long otherProfessorClassId = 102L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", "PROFESSOR");
        session.setAttribute("userId", professorId);

        // Bezbednosna provera: Vraćamo praznu listu, simulirajući da čas ne pripada profesoru
        when(scheduledClassService.findClassesByProfessorId(professorId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(post("/professor/class/{classId}/generate-code", otherProfessorClassId).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor/dashboard"))
                .andExpect(flash().attribute("error", "Pokušavate da generišete kod za čas koji Vam ne pripada!"));
    }
}