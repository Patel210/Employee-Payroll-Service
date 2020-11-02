package com.capgemini.jsonserverrestassured;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.capgemini.exceptions.DatabaseException;
import com.capgemini.payrolldata.EmployeePayrollData;
import com.capgemini.payrollservice.EmployeePayrollService;
import com.capgemini.payrollservice.EmployeePayrollService.IOService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
	
	private Map<Integer, Boolean> addMultipleEmployeesToJsonServer(List<EmployeePayrollData> employeePayrollDataList) {
		Map<Integer, Boolean> employeeInsertionStatus = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> employeeAdditionStatusCodes = new HashMap<Integer, Boolean>();
		employeePayrollDataList.forEach(employeePayrollData -> {
			employeeInsertionStatus.put(employeePayrollData.hashCode(), false);
			Runnable task = () -> {
				System.out.println("Employee Being added: " + Thread.currentThread().getName());
				
				Response response = addEmployeeToJsonServer(employeePayrollData);
				int statusCode = response.getStatusCode();
				if (statusCode == 201) {
					employeeInsertionStatus.put(employeePayrollData.hashCode(), true);
					String responseAsString = response.asString();
					JsonObject jsonObject = new Gson().fromJson(responseAsString, JsonObject.class);
					int id = jsonObject.get("id").getAsInt();
					employeePayrollData.setId(id);
					employeeAdditionStatusCodes.put(employeePayrollData.hashCode(), true);
				}
				else {
					employeeAdditionStatusCodes.put(employeePayrollData.hashCode(), false);
				}
				System.out.println("Employee added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.getName());
			thread.start();
		});
		
		while (employeeInsertionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		return employeeAdditionStatusCodes;
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
		
		String responseAsString = response.asString();
		JsonObject jsonObject = new Gson().fromJson(responseAsString, JsonObject.class);
		int id = jsonObject.get("id").getAsInt();
		employeePayrollData.setId(id);
		employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		long enteries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(2, enteries);
	}
	
	@Test
	public void givenMultipleEmployee_OnPost_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		List<EmployeePayrollData> employeePayrollDataList = new ArrayList<EmployeePayrollData>() {{
			add(new EmployeePayrollData(0, "Daksh", "Santa Cruz, Mumbai", 60000,
				LocalDate.now(), "M".charAt(0), "Capgemini", "Sales"));
			add(new EmployeePayrollData(0, "Arvind", "Hauz Khas, New Delhi", 50000,
					LocalDate.now(), "M".charAt(0), "Capgemini", "Production"));
			add(new EmployeePayrollData(0, "Vaishali", "Whitefield, Bangalore", 45000,
					LocalDate.now(), "F".charAt(0), "Capgemini", "Finance"));
		}};
		
		Map<Integer, Boolean> employeeAdditionStatusCodes = addMultipleEmployeesToJsonServer(employeePayrollDataList);
		assertFalse(employeeAdditionStatusCodes.containsValue(false));
		employeePayrollDataList.forEach(employeePayrollData -> {
			if(employeeAdditionStatusCodes.get(employeePayrollData.hashCode())) {
				employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
			}
		});
		long enteries = employeePayrollService.countEntries(IOService.REST_IO);
		assertEquals(5, enteries);
	}
}
