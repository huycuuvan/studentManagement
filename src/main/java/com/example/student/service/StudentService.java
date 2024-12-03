package com.example.student.service;

import com.example.student.entity.Student;
import com.example.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    private Stack<StudentAction> actionHistory = new Stack<>();

    private class StudentAction {
        private ActionType type;
        private Student student;
        
        public StudentAction(ActionType type, Student student) {
            this.type = type;
            this.student = student;
        }
    }
    
    private enum ActionType {
        ADD, UPDATE, DELETE
    }

    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new IllegalStateException("Danh sách sinh viên trống");
        }
        return students;
    }

    public void addStudent(Student student) {
        if (studentRepository.existsById(student.getStudentNumber())) {
            throw new RuntimeException("Mã số sinh viên đã tồn tại");
        }
        studentRepository.save(student);
        actionHistory.push(new StudentAction(ActionType.ADD, student));
    }

    public Student findStudentById(String studentNumber) {
        final String id = studentNumber.trim();
        return studentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên có mã số " + id));
    }

    public void updateStudent(Student student) {
        final String studentNumber = student.getStudentNumber().trim();
        if (!studentRepository.existsById(studentNumber)) {
            throw new RuntimeException("Không tìm thấy sinh viên có mã số " + studentNumber);
        }
        Student oldStudent = studentRepository.findById(studentNumber).get();
        studentRepository.save(student);
        actionHistory.push(new StudentAction(ActionType.UPDATE, oldStudent));
    }

    public void deleteStudent(String studentNumber) {
        final String id = studentNumber.trim();
        try {
            if (!studentRepository.existsById(id)) {
                throw new RuntimeException("Không tìm thấy sinh viên có mã số " + id);
            }
            Student deletedStudent = studentRepository.findById(id).get();
            studentRepository.deleteById(id);
            actionHistory.push(new StudentAction(ActionType.DELETE, deletedStudent));
        } catch (Exception e) {
            throw new RuntimeException("Không thể xóa sinh viên: " + e.getMessage());
        }
    }

    public List<Student> findByFullNameContaining(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name);
    }

    public List<Student> findByMark(double mark) {
        return studentRepository.findByMark(mark);
    }

    public List<Student> getAllStudentsSortedByMarkQuick() {
        try {
            List<Student> students = new ArrayList<>(studentRepository.findAll());
            if (!students.isEmpty()) {
                quickSortByMark(students, 0, students.size() - 1);
            }
            return students;
        } catch (Exception e) {
            throw new RuntimeException("Error sorting students: " + e.getMessage());
        }
    }

    private void quickSortByMark(List<Student> students, int low, int high) {
        if (low < high) {
            int pi = partitionByMark(students, low, high);
            quickSortByMark(students, low, pi - 1);
            quickSortByMark(students, pi + 1, high);
        }
    }

    private int partitionByMark(List<Student> students, int low, int high) {
        double pivot = students.get(high).getMark();
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (students.get(j).getMark() <= pivot) {
                i++;
                Collections.swap(students, i, j);
            }
        }
        Collections.swap(students, i + 1, high);
        return i + 1;
    }

    public List<Student> findStudentsByMarkBinarySearch(double targetMark) {
        try {
            List<Student> sortedStudents = getAllStudentsSortedByMarkQuick();
            List<Student> result = new ArrayList<>();
            int index = binarySearchMark(sortedStudents, targetMark);
            
            if (index != -1) {
                int left = index;
                int right = index;
                
                while (left > 0 && Math.abs(sortedStudents.get(left - 1).getMark() - targetMark) < 0.001) {
                    left--;
                }
                
                while (right < sortedStudents.size() - 1 && 
                       Math.abs(sortedStudents.get(right + 1).getMark() - targetMark) < 0.001) {
                    right++;
                }
                
                result.addAll(sortedStudents.subList(left, right + 1));
            }
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm kiếm nhị phân: " + e.getMessage());
        }
    }

    private int binarySearchMark(List<Student> students, double targetMark) {
        int left = 0;
        int right = students.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            double midMark = students.get(mid).getMark();
            
            if (Math.abs(midMark - targetMark) < 0.001) {
                return mid;
            }
            
            if (midMark < targetMark) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    public List<Student> getAllStudentsSortedByNameMerge() {
        try {
            List<Student> students = new ArrayList<>(studentRepository.findAll());
            mergeSortByName(students, 0, students.size() - 1);
            return students;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi sắp xếp tên bằng Merge Sort: " + e.getMessage());
        }
    }

    private void mergeSortByName(List<Student> students, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSortByName(students, left, mid);
            mergeSortByName(students, mid + 1, right);
            merge(students, left, mid, right);
        }
    }

    private void merge(List<Student> students, int left, int mid, int right) {
        List<Student> leftList = new ArrayList<>(students.subList(left, mid + 1));
        List<Student> rightList = new ArrayList<>(students.subList(mid + 1, right + 1));
        
        int i = 0, j = 0, k = left;
        
        while (i < leftList.size() && j < rightList.size()) {
            if (leftList.get(i).getFullName().compareToIgnoreCase(rightList.get(j).getFullName()) <= 0) {
                students.set(k++, leftList.get(i++));
            } else {
                students.set(k++, rightList.get(j++));
            }
        }
        
        while (i < leftList.size()) {
            students.set(k++, leftList.get(i++));
        }
        
        while (j < rightList.size()) {
            students.set(k++, rightList.get(j++));
        }
    }

    public List<Student> sortByMarkQuick() {
        List<Student> students = new ArrayList<>(studentRepository.findAll());
        if (students.isEmpty()) {
            return students;
        }
        quickSortByMark(students, 0, students.size() - 1);
        return students;
    }

    public List<Student> sortByNameMerge() {
        List<Student> students = new ArrayList<>(studentRepository.findAll());
        if (students.isEmpty()) {
            return students;
        }
        
        students.sort((s1, s2) -> {
            String[] name1 = s1.getFullName().split("\\s+");
            String[] name2 = s2.getFullName().split("\\s+");
            
            String firstName1 = name1[name1.length - 1];
            String firstName2 = name2[name2.length - 1];
            
            int compareResult = firstName1.compareToIgnoreCase(firstName2);
            
            if (compareResult == 0) {
                return s1.getFullName().compareToIgnoreCase(s2.getFullName());
            }
            
            return compareResult;
        });
        
        return students;
    }

    public void undoLastAction() {
        if (actionHistory.isEmpty()) {
            throw new RuntimeException("Không có thao tác nào để hoàn tác");
        }

        StudentAction lastAction = actionHistory.pop();
        try {
            switch (lastAction.type) {
                case ADD:
                    studentRepository.deleteById(lastAction.student.getStudentNumber());
                    break;
                case UPDATE:
                case DELETE:
                    studentRepository.save(lastAction.student);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể hoàn tác thao tác: " + e.getMessage());
        }
    }

    public StudentAction peekLastAction() {
        if (actionHistory.isEmpty()) {
            throw new RuntimeException("Không có thao tác nào trong lịch sử");
        }
        return actionHistory.peek();
    }

    public int getHistorySize() {
        return actionHistory.size();
    }
}
