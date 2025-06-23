package com.ognjen.eattendance.repository;

import com.ognjen.eattendance.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StudentRepository {
    private final List<Student> students = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Student> findAll() { return new ArrayList<>(students); }

    public Optional<Student> findById(Long id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public Optional<Student> findByUsername(String username) {
        return students.stream().filter(s -> s.getUsername().equals(username)).findFirst();
    }
    public void deleteById(Long id) {
        students.removeIf(student -> student.getId().equals(id));
    }
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(idCounter.incrementAndGet());
            students.add(student);
        } else {
            findById(student.getId()).ifPresent(existing -> {
                int index = students.indexOf(existing);
                if (student.getPassword() == "" || student.getPassword() == null) {
                    student.setPassword(existing.getPassword());
                }
                students.set(index, student);
            });
        }
        return student;
    }
}