package com.example.education.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.education.dto.DepartmentDTO;

@FeignClient(name = "department-service")
public interface DepartmentClient {

    @GetMapping("/department/all")
    List<DepartmentDTO> getAllDepartments();
}

