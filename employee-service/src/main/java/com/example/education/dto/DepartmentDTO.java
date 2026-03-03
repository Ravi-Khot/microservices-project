package com.example.education.dto;

public class DepartmentDTO {

    private Long id;
    private String departmentName;
    private String location;

    
    /**
	 * @param id
	 * @param departmentName
	 * @param location
	 */
	public DepartmentDTO(Long id, String departmentName, String location) {
		this.id = id;
		this.departmentName = departmentName;
		this.location = location;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
