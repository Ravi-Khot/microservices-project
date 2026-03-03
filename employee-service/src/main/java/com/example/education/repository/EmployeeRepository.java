package com.example.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.education.entity.Employee;

public interface EmployeeRepository
extends JpaRepository<Employee, Long> {
}

