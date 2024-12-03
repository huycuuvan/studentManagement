package com.example.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @Column(name = "student_number")
    private String studentNumber;

    @Column(name = "full_name")
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Column(name = "mark")
    @Min(value = 0, message = "Điểm không được nhỏ hơn 0")
    @Max(value = 10, message = "Điểm không được lớn hơn 10")
    private double mark;

    // Custom getter for rank that calculates on-the-fly
    @Transient
    public String getRank() {
        if (mark >= 0 && mark < 5) {
            return "Fail";
        } else if (mark >= 5 && mark < 6.5) {
            return "Medium";
        } else if (mark >= 6.5 && mark < 7.5) {
            return "Good";
        } else if (mark >= 7.5 && mark < 9) {
            return "Very Good";
        } else if (mark >= 9 && mark <= 10) {
            return "Excellent";
        }
        return "Invalid";
    }
}