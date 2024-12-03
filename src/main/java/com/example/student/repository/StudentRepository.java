package com.example.student.repository;


import com.example.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    Optional<Student> findByStudentNumber(String studentNumber);
    boolean existsByStudentNumber(String studentNumber);
    List<Student> findByFullNameContainingIgnoreCaseOrStudentNumber(String fullName, String studentNumber);
    List<Student> findByFullNameContainingIgnoreCase(String name);
    List<Student> findByMark(double mark);
}

