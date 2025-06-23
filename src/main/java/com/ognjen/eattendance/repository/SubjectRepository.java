package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.Subject;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class SubjectRepository {

    private final List<Subject> subjects = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Subject> findAll() {
        return new ArrayList<>(subjects);
    }

    public Optional<Subject> findById(Long id) {
        return subjects.stream()
                .filter(subject -> subject.getId().equals(id))
                .findFirst();
    }

    public Subject save(Subject subject) {
        if (subject.getId() == null) {
            subject.setId(idCounter.incrementAndGet());
            subjects.add(subject);
        } else {
            findById(subject.getId()).ifPresent(existing -> {
                int index = subjects.indexOf(existing);
                subjects.set(index, subject);
            });
        }
        return subject;
    }

    public void deleteById(Long id) {
        subjects.removeIf(subject -> subject.getId().equals(id));
    }
}