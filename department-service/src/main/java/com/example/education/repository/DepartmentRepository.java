package com.example.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.education.entity.Department;

public interface DepartmentRepository
extends JpaRepository<Department, Long> {
}

