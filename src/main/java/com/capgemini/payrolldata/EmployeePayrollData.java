package com.capgemini.payrolldata;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {

	private int id;
	private String companyName;
	private String name;
	private String address;
	private double salary;
	private LocalDate startDate;
	private char gender;
	private String[] departments;

	public EmployeePayrollData(int empId, String name, double salary) {
		this.name = name;
		this.id = empId;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(int id, String companyName, String name, double salary, LocalDate startDate, char gender) {
		this(id, name, salary, startDate);
		this.companyName = companyName;
		this.gender = gender;
	}
	
	public EmployeePayrollData(int id, String name, String address, double salary, LocalDate startDate, char gender,
								String companyName, String ... departments) {
		this(id, companyName, name, salary, startDate, gender);
		this.departments = departments;
		this.address = address;
	}

	@Override
	public String toString() {
		return "Id: " + id + " Name: " + name + " Salary: " + salary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getAddress() {
		return address;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public String[] getDepartments() {
		return departments;
	}

	public char getGender() {
		return gender;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, companyName, name, address, salary, startDate, gender, departments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		return id == other.id  && getCompanyName().equals(other.getCompanyName()) && name.equals(other.name) && Double.compare(salary, other.salary) == 0;
	}
}
