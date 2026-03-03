package com.example.education.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.education.entity.Department;
import com.example.education.service.DepartmentService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController
@RequestMapping("/department") //set the base url path for all endpoint in this controller
//@CrossOrigin(origins = "http://localhost:4200")  //Allows requests from your Angular frontend (http://localhost:4200) to access backend (http://localhost:8080).
public class DepartmentController {

	 	@Autowired
	 	private Environment environment;
	 	
	 	@Autowired 
	    private DepartmentService service;

	    @PostMapping("/create")
	    public Department create(@RequestBody Department dept) {
	        return service.save(dept);
	    }

	    @GetMapping("/all")
	    public List<Department> getAll() {
	        return service.getAll();
	    }
	    
	    @GetMapping("/{id}")
	    public Department getById(@PathVariable Long id) {
	        return service.getById(id);
	    }
	    
	    @DeleteMapping("/delete/{id}")
	    public String deleteDepartment(@PathVariable Long id) {
	        service.deleteById(id);
	        return "Department deleted successfully";
	    }
	    
	    @GetMapping("/instance-info")
	    public String getInstanceInfo() {
	    	System.out.println("Port -------------------------------");
	        return "Response from port: " + environment.getProperty("local.server.port");
	    }
}
