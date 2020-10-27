package com.capgemini.fileioservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollFileIOService {
	private static String PAYROLL_FILE_NAME = "payroll-file.txt";

	/**
	 * Writes Employee payroll Data object to a file
	 */
	public void writeData(ArrayList<EmployeePayrollData> employeePayrollDataList) {
		StringBuffer employeeBuffer = new StringBuffer();
		employeePayrollDataList.forEach(employee -> {
			String employeeString = employee.toString().concat("\n");
			employeeBuffer.append(employeeString);
		});

		try {
			Files.write(Paths.get(PAYROLL_FILE_NAME), employeeBuffer.toString().getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Counts the entries in a file
	 */
	public long countEntries() {
		long entries = 0;
		try {
			entries = Files.lines(new File(PAYROLL_FILE_NAME).toPath()).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	/**
	 * Prints the date from the file
	 */
	public void printEmployeePayrollData() {
		try {
			Files.lines(Paths.get(PAYROLL_FILE_NAME)).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads Data from the file
	 */
	public ArrayList<EmployeePayrollData> readEmployeePayrollData(){
		ArrayList<EmployeePayrollData> employeePayrollDataList = new ArrayList<EmployeePayrollData>();
		try {
			Files.lines(Paths.get(PAYROLL_FILE_NAME)).forEach(line -> {
				line = line.trim();
				String[] empData = line.split("[\\s]{0,}[a-zA-Z]+[:][\\s]");
				employeePayrollDataList.add(new EmployeePayrollData(Integer.parseInt(empData[1]), empData[2], Double.parseDouble(empData[3])));
			});
		}catch(IOException e) {
			e.printStackTrace();
		}
		return employeePayrollDataList;
	}
}
