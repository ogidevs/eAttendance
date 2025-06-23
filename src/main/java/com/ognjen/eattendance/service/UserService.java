package com.ognjen.eattendance.service;

import com.ognjen.eattendance.model.Professor;
import com.ognjen.eattendance.model.Student;
import com.ognjen.eattendance.repository.InMemoryDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InMemoryDataRepository repository;

    public Optional<Student> getStudentByUsername(String username) {
        return repository.findStudentByUsername(username);
    }

    public Optional<Professor> getProfessorByUsername(String username) {
        return repository.findProfessorByUsername(username);
    }

    public Optional<Student> getStudentById(Long id) {
        return repository.findStudentById(id);
    }
}