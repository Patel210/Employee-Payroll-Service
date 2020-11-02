package com.capgemini.jsonserverrestassured;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollJsonServerRestAssuredTest {

	@Before
	public void SetUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	} 
	
	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee-payroll");
		System.out.println(response.asString());
		EmployeePayrollData[] array = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return array;
	}

	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String jsonString = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();	
		request.header("Content-Type","application/json");
		request.body(jsonString);
		return request.post("/employee-payroll"); 
	}
	
	@Test
	public void givenNewEmployee_OnPost_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		EmployeePayrollData employeePayrollData = new EmployeePayrollData(0, "Rashi", "Malta Road, Jaipur", 30000,
				LocalDate.now(), "F".charAt(0), "Capgemini", "Sales", "Marketing");
		Response response = addEmployeeToJsonServer(employeePayrollData);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);
		
		employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		long enteries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(2, enteries);
	}
}
