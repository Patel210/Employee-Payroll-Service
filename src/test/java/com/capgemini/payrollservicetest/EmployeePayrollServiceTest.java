package com.capgemini.payrollservicetest;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.capgemini.exceptions.DatabaseException;
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
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldUpdateInTheDatabaseRecord() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		try {
			employeePayrollService.updateEmployeeSalary(2, 3000000.0, IOService.DB_IO);
			boolean result = employeePayrollService.isEmployeePayrollInSyncWithDB(2);
			assertTrue(result);
		} catch (DatabaseException e) {}
	}
	
	@Test
	public void givenDateRange_WhenRetrievedFromDB_ShouldMatchTotalCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		ArrayList<EmployeePayrollData> list = employeePayrollService.readEmployeePayrollDataForDateRange(IOService.DB_IO, startDate, endDate);
		assertEquals(3, list.size());
	}
	
	@Test
	public void givenPayrollDB_WhenSumOfSalaryRetrievedByGender_ShouldReturnCorrectResult() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Map<String, Double> sumOfSalariesByGender = employeePayrollService.readSumOfSalariesByGender(IOService.DB_IO);
		assertTrue(sumOfSalariesByGender.get("M").equals(4000000.00) && sumOfSalariesByGender.get("F").equals(3000000.00));
	}
	
	@Test
	public void givenPayrollDB_WhenAverageSalaryRetrievedByGender_ShouldReturnCorrectResult() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Map<String, Double> avgSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		assertTrue(avgSalaryByGender.get("M").equals(2000000.00) && avgSalaryByGender.get("F").equals(3000000.00));
	}
	
	@Test
	public void givenPayrollDB_WhenMinimumSalaryRetrievedByGender_ShouldReturnCorrectResult() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Map<String, Double> minSalaryByGender = employeePayrollService.readMinSalaryByGender(IOService.DB_IO);
		assertTrue(minSalaryByGender.get("M").equals(1000000.00) && minSalaryByGender.get("F").equals(3000000.00));
	}
	 
	@Test
	public void givenPayrollDB_WhenMaximumSalaryRetrievedByGender_ShouldReturnCorrectResult() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Map<String, Double> maxSalaryByGender = employeePayrollService.readMaxSalaryByGender(IOService.DB_IO);
		assertTrue(maxSalaryByGender.get("M").equals(3000000.00) && maxSalaryByGender.get("F").equals(3000000.00));
	} 
	
	@Test
	public void givenPayrollDB_WhenEmployeeCountRetrievedByGender_ShouldReturnCorrectResult() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Map<String, Integer> countByGender = employeePayrollService.readEmployeeCountByGender(IOService.DB_IO);
		assertTrue(countByGender.get("M").equals(2) && countByGender.get("F").equals(1));
	}
	 
	@Test
	public void givenNewEmployee_WhenAddedToDB_ShouldBeInSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		try {
			int id = employeePayrollService.addEmployeeToPayroll("Mark", "Brooklyn Road, New York", 4000000.0, 'M', LocalDate.now(), "Capgemini", "Sales" , "Marketing");
			boolean result = employeePayrollService.isEmployeePayrollInSyncWithDB(id);
			assertTrue(result);
		} catch (DatabaseException e) {System.out.println(e.getMessage());}
	}
	
	@Test
	public void givenEmployee_WhenRemovedFromDB_ShouldReturnCorrectEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		employeePayrollService.removeEmployee(1, IOService.DB_IO);
		assertEquals(3, employeePayrollService.employeeDataSize());
	}
	
	@Test
	public void givenEmployeePayrollDB_WhenRetrieved_ShouldReturnOnlyActiveEmployees() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		assertEquals(3, employeePayrollService.employeeDataSize());
	}
	
	@Test
	public void givenMultipleEmployee_WhenAddedToDB_ShouldMatchTotalEntries() {
		EmployeePayrollData[] arrayOfEmployees = { 
				new EmployeePayrollData(0, "Tushar", "Malta Road, Jaipur", 1500000.0, LocalDate.now(), "M".charAt(0), "BridgeLabz", "R&D", "Production"),
				new EmployeePayrollData(0, "Vaibhavi", "Santa Cruz, Jaipur", 3000000.0, LocalDate.now(), "F".charAt(0), "Capgemini", "Marketing"),
				new EmployeePayrollData(0, "Adeesh", "WhiteField, Bangalore", 4000000.0, LocalDate.now(), "M".charAt(0), "BridgeLabz", "Production")
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeeToPayroll(Arrays.asList(arrayOfEmployees));
		Instant end = Instant.now();
		System.out.println("Duration for complete execution: " + Duration.between(start, end));
		assertEquals(6, employeePayrollService.employeeDataSize());
	}
	
	@Test
	public void givenMultipleEmployee_WhenAddedUsingMultiThreadingToDB_ShouldMatchTotalEntries() {
		EmployeePayrollData[] arrayOfEmployees = { 
				new EmployeePayrollData(0, "Parth", "Malta Road, Jaipur", 1500000.0, LocalDate.now(), "M".charAt(0), "BridgeLabz", "R&D", "Production"),
				new EmployeePayrollData(0, "Sanya", "Santa Cruz, Mumbai", 3000000.0, LocalDate.now(), "F".charAt(0), "Capgemini", "Marketing"),
				new EmployeePayrollData(0, "Salil", "WhiteField, Bangalore", 4000000.0, LocalDate.now(), "M".charAt(0), "BridgeLabz", "Production"),
				new EmployeePayrollData(0, "Sunil", "Satelite Road, Ahmedabad", 2500000.0, LocalDate.now(), "M".charAt(0), "Barclays", "R&D", "Production")
		};
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmployees));
		Instant threadEnd = Instant.now();
		System.out.println("Duration for complete execution with Threads: " + Duration.between(threadStart, threadEnd));
		assertEquals(10, employeePayrollService.employeeDataSize());
	}
	
	@Test
	public void givenNewSalariesForMultipleEmployees_WhenUpdatedInDB_ShouldBeInSyncWithDB() {
		Map<Integer, Double> newSalaries = new HashMap<Integer, Double>();
		newSalaries.put(7, 1000000.0);
		newSalaries.put(8, 2300000.0);
		newSalaries.put(9, 3000000.0);
		newSalaries.put(10, 1500000.0);
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeeData(IOService.DB_IO);
		Instant threadStart = Instant.now();
		boolean result = employeePayrollService.updateEmployeeSalary(newSalaries);
		Instant threadEnd = Instant.now();
		System.out.println("Duration for complete execution with Threads: " + Duration.between(threadStart, threadEnd));
		assertTrue(result);
	}
}
