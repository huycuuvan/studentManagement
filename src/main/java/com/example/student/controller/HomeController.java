package com.example.student.controller;

import com.example.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/")
    public String home(Model model) {
        long totalStudents = studentService.getAllStudents().size();
        model.addAttribute("totalStudents", totalStudents);
        return "home";
    }
} 