package com.capgemini.payrollservice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.capgemini.databaseservice.EmployeePayrollDBService;
import com.capgemini.exceptions.DatabaseException;
import com.capgemini.fileioservice.EmployeePayrollFileIOService;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollService {
	
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private ArrayList<EmployeePayrollData> employeePayrollDataList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService(ArrayList<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDBService = EmployeePayrollDBService.createInstance();
		this.employeePayrollDataList = employeePayrollDataList;
	}

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.createInstance();
		this.employeePayrollDataList = new ArrayList<EmployeePayrollData>();
	}

	/**
	 * Reads from console, file, and DB
	 * @throws DatabaseException 
	 */
	public ArrayList<EmployeePayrollData> readEmployeeData(IOService ioService) {
		if (ioService.equals(IOService.CONSOLE_IO)) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the employee data\n\nEnter Employee name: ");
			String empName = sc.next();
			System.out.println("Enter the employee id:");
			int empId = sc.nextInt();
			System.out.println("Enter the employee salary:");
			double salary = sc.nextDouble();
			employeePayrollDataList.add(new EmployeePayrollData(empId, empName, salary));
			sc.close();
		} 
		if (ioService.equals(IOService.FILE_IO)) {
			this.employeePayrollDataList = new EmployeePayrollFileIOService().readEmployeePayrollData();
		}
		if(ioService.equals(IOService.DB_IO)) {
			try {
				this.employeePayrollDataList = employeePayrollDBService.readEmployeeData();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return employeePayrollDataList;
	}
	
	/**
	 * @param service
	 * @param startDate
	 * @param endDate
	 * @return List of Employee Payroll data between given date range
	 */
	public ArrayList<EmployeePayrollData> readEmployeePayrollDataForDateRange(IOService service, LocalDate startDate, LocalDate endDate) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getEmployeeDataForDateRange(startDate, endDate);
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	

	/**
	 * @param service
	 * @return Map containing gender as key and sum of salaries as value
	 */
	public Map<String, Double> readSumOfSalariesByGender(IOService service) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getSumOfSalariesByGender();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * @param service
	 * @return Map containing gender as key and avg salary as value
	 */
	public Map<String, Double> readAverageSalaryByGender(IOService service) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getAvgSalaryByGender();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * @param service
	 * @return Map containing gender as key and min as value
	 */
	public Map<String, Double> readMinSalaryByGender(IOService service) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getMinSalaryByGender();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * @param service
	 * @return Map containing gender as key and max as value
	 */
	public Map<String, Double> readMaxSalaryByGender(IOService service) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getMaxSalaryByGender();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * @param service
	 * @return Map containing gender as key and employee count as value
	 */
	public Map<String, Integer> readEmployeeCountByGender(IOService service) {
		if(service.equals(IOService.DB_IO)) {
			try {
				return employeePayrollDBService.getCountByGender();
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		return null;
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
	 * Add multiple employees to the DB
	 */
	public void addEmployeeToPayroll(List<EmployeePayrollData> employeeList) {
		employeeList.forEach(employeePayrollData -> {
			try {
				System.out.println("Employee Being added: " + employeePayrollData.getEmpName());
				addEmployeeToPayroll(employeePayrollData.getEmpName(), employeePayrollData.getAddress(), 
						employeePayrollData.getSalary(), employeePayrollData.getGender(), employeePayrollData.getStartDate(), employeePayrollData.getCompanyName(), employeePayrollData.getDepartments());
				System.out.println("Employee added: " + employeePayrollData.getEmpName());
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		});
	}
	
	/**
	 * Add multiple employees to the DB using threads
	 */
	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeeList) {
		Map<Integer, Boolean> employeeInsertionStatus = new HashMap<Integer, Boolean>();
		employeeList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeInsertionStatus.put(employeePayrollData.hashCode(), false);
			try {
				System.out.println("Employee Being added: " + Thread.currentThread().getName());
				addEmployeeToPayroll(employeePayrollData.getEmpName(), employeePayrollData.getAddress(), 
						employeePayrollData.getSalary(), employeePayrollData.getGender(), employeePayrollData.getStartDate(),
						employeePayrollData.getCompanyName(), employeePayrollData.getDepartments());
				employeeInsertionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("Employee added: " + Thread.currentThread().getName());
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
			};
			Thread thread = new Thread(task, employeePayrollData.getEmpName());
			thread.start();
		});
		while (employeeInsertionStatus.containsValue(false)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * To add Employee Payroll Data to Database;
	 */
	public void addEmployeeToPayroll(String name, String address, double salary, char gender, LocalDate startDate,
									String companyName, String ... departments) throws DatabaseException {
		employeePayrollDataList.add(employeePayrollDBService.addEmployeePayrollToDB(name, address, salary, gender, startDate, companyName, departments));
	}

	/**
	 * @param name
	 * @param salary
	 * @throws DatabaseException
	 * To Update Employee Salary
	 */
	public void updateEmployeeSalary(String name, double salary) throws DatabaseException {
		int result = employeePayrollDBService.updateEmployeeSalaryUsingPreparedStatement(name, salary);
		if (result != 0) {
			EmployeePayrollData employeePayrollData = getEmployeeData(name);
			if(employeePayrollData != null) employeePayrollData.setSalary(salary);
		}
	}
	
	/**
	 * Remove employee with given id from the DB
	 */
	public void removeEmployeeFromDB(int id) {
		try {
			employeePayrollDBService.removeEmployee(id);
			employeePayrollDataList = employeePayrollDataList.stream()
															 .filter(employeePayrollData -> employeePayrollData.getEmpId() != id)
															 .collect(Collectors.toCollection(ArrayList::new));
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
		}	
	}
	
	/**
	 * @param name
	 * @return T/F whether Payroll is in sync with DB or not
	 * @throws DatabaseException
	 */
	public boolean isEmployeePayrollInSyncWithDB(String name) throws DatabaseException {
		ArrayList<EmployeePayrollData> list = employeePayrollDBService.getEmployeeData(name);
		return list.get(0).equals(getEmployeeData(name));
	}

	/**
	 * @param name
	 * @returns Employee Payroll Data Object with given employee name
	 */
	private EmployeePayrollData getEmployeeData(String name) {
		return employeePayrollDataList.stream()
									  .filter(employeePayrollData -> employeePayrollData.getEmpName().equals(name))
									  .findFirst()
									  .orElse(null);
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
