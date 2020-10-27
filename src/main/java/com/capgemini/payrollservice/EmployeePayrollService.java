package com.capgemini.payrollservice;

import java.util.ArrayList;
import java.util.Scanner;

import com.capgemini.fileioservice.EmployeePayrollFileIOService;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private ArrayList<EmployeePayrollData> employeePayrollDataList;

	public EmployeePayrollService(ArrayList<EmployeePayrollData> employeePayrollDataList) {
		super();
		this.employeePayrollDataList = employeePayrollDataList;
	}

	public EmployeePayrollService() {
		this.employeePayrollDataList = new ArrayList<EmployeePayrollData>();
	}

	/**
	 * Reads from console
	 */
	public void readEmployeeData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the employee data\n\nEnter Employee name: ");
			String empName = sc.next();
			System.out.println("Enter the employee id:");
			long empId = sc.nextLong();
			System.out.println("Enter the employee salary:");
			long salary = sc.nextLong();
			employeePayrollDataList.add(new EmployeePayrollData(empId, empName, salary));
			sc.close();
		} else if (ioService.equals(IOService.FILE_IO)) {
			this.employeePayrollDataList = new EmployeePayrollFileIOService().readEmployeePayrollData();
		}
	}

	/**
	 * Writes to file or consoles
	 */
	public void writeEmployeeData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			employeePayrollDataList.forEach(System.out::println);
		} else if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollDataList);
		}

	}

	/**
	 * Counts the entries
	 */
	public long countEntries(IOService ioService) {
		return new EmployeePayrollFileIOService().countEntries();
	}

	/**
	 * Prints employee Payroll Data
	 */
	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printEmployeePayrollData();
		} else if (ioService.equals(IOService.CONSOLE_IO)) {
			employeePayrollDataList.forEach(System.out::println);
		}
	}

	/**
	 *Returns the size of the employee payroll data list
	 */
	public long employeeDataSize() {
		return employeePayrollDataList.size();
	}
}
