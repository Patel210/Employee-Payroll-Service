package com.capgemini.payrolldata;

import java.time.LocalDate;

public class EmployeePayrollData {

	private int empId;
	private String companyName;
	private String empName;
	private double salary;
	private LocalDate startDate;
	private char gender;
	private String[] departments;

	public EmployeePayrollData(int empId, String empName, double salary) {
		this.empName = empName;
		this.empId = empId;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender) {
		this(id, name, salary, startDate);
		this.gender = gender;
	}
	
	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate, char gender,
								String companyName, String[] departments) {
		this(id, name, salary, startDate, gender);
		this.departments = departments;
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "Id: " + empId + " Name: " + empName + " Salary: " + salary;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		return empId == other.empId && empName.equals(other.empName) && Double.compare(salary, other.salary) == 0
				&& startDate.isEqual(other.startDate) && gender == other.gender;
	}
}
