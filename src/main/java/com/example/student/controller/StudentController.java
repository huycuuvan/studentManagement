package com.example.student.controller;

import com.example.student.entity.Student;
import com.example.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("isEdit", false);
        return "students/form";
    }

    @PostMapping("/add")
    public String addStudent(@Valid @ModelAttribute Student student, 
                           BindingResult result, 
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "students/form";
        }
        try {
            studentService.addStudent(student);
            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", false);
            return "students/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model) {
        try {
            Student student = studentService.findStudentById(id.trim());
            model.addAttribute("student", student);
            model.addAttribute("isEdit", true);
            return "students/form";
        } catch (Exception e) {
            return "redirect:/students";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable("id") String id,
                              @Valid @ModelAttribute Student student,
                              BindingResult result,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "students/form";
        }
        try {
            student.setStudentNumber(id.trim());
            studentService.updateStudent(student);
            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("isEdit", true);
            return "students/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable("id") String id, 
                              RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id.trim());
            redirectAttributes.addFlashAttribute("success", 
                "Đã xóa sinh viên có mã số " + id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Không thể xóa sinh viên: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/search/name")
    public String searchByName(@RequestParam String query, Model model) {
        try {
            model.addAttribute("students", 
                studentService.findByFullNameContaining(query));
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/search/mark")
    public String searchByMark(@RequestParam double mark, Model model) {
        try {
            model.addAttribute("students", 
                studentService.findByMark(mark));
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/sorted/mark/quick")
    public String sortByMarkQuick(Model model) {
        try {
            model.addAttribute("students", studentService.sortByMarkQuick());
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }

    @GetMapping("/sorted/name/merge")
    public String sortByNameMerge(Model model) {
        try {
            model.addAttribute("students", studentService.sortByNameMerge());
            return "students/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/students";
        }
    }
}
