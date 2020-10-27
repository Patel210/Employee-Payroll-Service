package com.capgemini.payrollservicetest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] employeeArray = { new EmployeePayrollData(1, "Praket Parth", 1000000),
				new EmployeePayrollData(1, "Trisha Chaudhary", 950000),
				new EmployeePayrollData(1, "Vishal Gupta", 1100000) };
		ArrayList<EmployeePayrollData> empPayrollDataList = new ArrayList<EmployeePayrollData>();
		for (EmployeePayrollData employeeData : employeeArray) {
			empPayrollDataList.add(employeeData);
		}
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(empPayrollDataList);
		employeePayrollService.writeEmployeeData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
		assertEquals(3, employeePayrollService.countEntries(IOService.FILE_IO));
	}
	
	@Test
	public void givenEmployeeDataOnAFile_WhenRead_ShouldMatchTheEntries() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.FILE_IO);
		assertEquals(3, employeePayrollService.employeeDataSize());
	}

	@Test
	public void givenEmployeePayrollDB_WhenRetrieved_ShouldMatchTotalEntries() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		assertEquals(3, employeePayrollService.employeeDataSize());
	}
}
