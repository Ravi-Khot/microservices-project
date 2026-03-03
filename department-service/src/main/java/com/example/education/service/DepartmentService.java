package com.example.education.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.education.entity.Department;
import com.example.education.exception.EmployeeNotFoundException;
import com.example.education.repository.DepartmentRepository;

@Service
public class DepartmentService {

	 @Autowired
	    private DepartmentRepository repository;

	    public Department save(Department dept) {
	        return repository.save(dept);
	    }

	    public List<Department> getAll() {
	        return repository.findAll();
	    }
	    
	    public Department getById(Long id) {
	        return repository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
	    }
	    
	    public void deleteById(Long id) {
	        repository.deleteById(id);
	    }
}


