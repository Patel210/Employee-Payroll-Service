package com.capgemini.payrolldata;

public class EmployeePayrollData {
	private long empId;
	private String empName;
	private long salary;

	public EmployeePayrollData(long empId, String empName, long salary) {
		this.empName = empName;
		this.empId = empId;
		this.salary = salary;
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

	public void setEmpId(long empId) {
		this.empId = empId;
	}

	public long getSalary() {
		return salary;
	}

	public void setSalary(long salary) {
		this.salary = salary;
	}

}
