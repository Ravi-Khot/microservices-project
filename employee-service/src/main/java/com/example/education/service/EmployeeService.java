package com.example.education.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.education.client.DepartmentClient;
import com.example.education.dto.DepartmentDTO;
import com.example.education.dto.EmployeeDepartmentResponse;
import com.example.education.entity.Employee;
import com.example.education.exception.EmployeeNotFoundException;
import com.example.education.repository.EmployeeRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class EmployeeService {

	@Autowired
	private DepartmentClient departmentClient;
	
	@Autowired
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // CREATE
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // GET ALL
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    // GET BY ID  
    public Employee employeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() ->
                new EmployeeNotFoundException(
                    "Employee not found with id: " + id
                )
            );
    }

    // UPDATE
    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // DELETE  ⭐ HERE ⭐
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(
                "Employee not found with id: " + id
            );
        }
        employeeRepository.deleteById(id);
    }
    
    
//    public EmployeeDepartmentResponse getEmployeeWithDepartment(Long id) {
//
//        // 1️⃣ Get employee from DB
//        Employee emp = employeeRepository.findById(id)
//                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
//
//        // 2️⃣ Call department-service using Feign
//        List<DepartmentDTO> departments = departmentClient.getAllDepartments();
//
//        // 3️⃣ Build response DTO
//        EmployeeDepartmentResponse response = new EmployeeDepartmentResponse();
//        response.setId(emp.getId());
//        response.setName(emp.getName());
//        response.setAge(emp.getAge());
//        response.setDepartment(emp.getDepartment());
//        response.setDepartments(departments);
//
//        return response;
//    }
    
    // 🔥 CIRCUIT BREAKER ADDED HERE
    @CircuitBreaker(name = "departmentService", fallbackMethod = "fallbackMethod")
    public EmployeeDepartmentResponse getEmployeeWithDepartment(Long id) {

        // 1️⃣ Get employee from DB
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        // 2️⃣ Call department-service using Feign
        List<DepartmentDTO> departments = departmentClient.getAllDepartments();

        // 3️⃣ Build response
        EmployeeDepartmentResponse response = new EmployeeDepartmentResponse();
        response.setId(emp.getId());
        response.setName(emp.getName());
        response.setAge(emp.getAge());
        response.setDepartment(emp.getDepartment());
        response.setDepartments(departments);

        return response;
    }

    // 🔥 FALLBACK METHOD (IMPORTANT)
    public EmployeeDepartmentResponse fallbackMethod(Long id, Exception ex) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        EmployeeDepartmentResponse response = new EmployeeDepartmentResponse();
        response.setId(emp.getId());
        response.setName(emp.getName());
        response.setAge(emp.getAge());
        response.setDepartment(emp.getDepartment());

        // When department-service is DOWN
        response.setDepartments(List.of(
                new DepartmentDTO(null,
                        "Department Service Down",
                        "Not Available")
        ));

        return response;
    }

}


