package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.Professor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProfessorRepository {
    private final List<Professor> professors = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Professor> findAll() { return new ArrayList<>(professors); }

    public Optional<Professor> findById(Long id) {
        return professors.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Optional<Professor> findByUsername(String username) {
        return professors.stream().filter(p -> p.getUsername().equals(username)).findFirst();
    }

    public void deleteById(Long id) {
        professors.removeIf(prof -> prof.getId().equals(id));
    }

    public Professor save(Professor professor) {
        if (professor.getId() == null) {
            professor.setId(idCounter.incrementAndGet());
            professors.add(professor);
        } else {
            findById(professor.getId()).ifPresent(existing -> {
                int index = professors.indexOf(existing);
                if (professor.getPassword() == "" || professor.getPassword() == null) {
                    professor.setPassword(existing.getPassword());
                }
                professors.set(index, professor);
            });
        }
        return professor;
    }
}
