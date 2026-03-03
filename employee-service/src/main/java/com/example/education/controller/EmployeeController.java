package com.example.education.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.education.dto.EmployeeDepartmentResponse;
import com.example.education.entity.Employee;
import com.example.education.service.EmployeeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/employee") //set the base url path for all endpoint in this controller
//@CrossOrigin(origins = "http://localhost:4200")  //Allows requests from your Angular frontend (http://localhost:4200) to access backend (http://localhost:8080).
public class EmployeeController {

    private final EmployeeService employeeService;

    // ✅ Constructor injection (best practice)
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@Valid @RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @GetMapping("/fetch/all")
    public List<Employee> getAllEmployee() {
    	System.out.println("Fetching all details of employee");
        return employeeService.getAllEmployee();
    }

    @GetMapping("/fetch/{id}")
    public Employee getEmployeeById(@PathVariable("id") Long id) {
        return employeeService.employeeById(id);
    }


    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        // set the id from URL to ensure correct employee is updated
        employee.setId(id);
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployeeById(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
    }
    
//    @GetMapping("/fetch-with-department/{id}")
//    public Employee getEmployeeWithDepartment(@PathVariable Long id) {
//        return employeeService.getEmployeeWithDepartment(id);
//    }
//    @GetMapping("/fetch-with-department/{id}")
//    public Employee getEmployeeWithDepartment(@PathVariable Long id) {
//        return employeeService.getEmployeeWithDepartment(id);
//    }
    
    @GetMapping("/fetch-with-department/{id}")
    public EmployeeDepartmentResponse fetchEmployeeWithDepartment(@PathVariable Long id) {
        return employeeService.getEmployeeWithDepartment(id);
    }

}
