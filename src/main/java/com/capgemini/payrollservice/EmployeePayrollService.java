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

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList) {
		employeePayrollDBService = EmployeePayrollDBService.createInstance();
		this.employeePayrollDataList = new ArrayList<EmployeePayrollData>(employeePayrollDataList);
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
				System.out.println("Employee Being added: " + employeePayrollData.getName());
				addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getAddress(), 
						employeePayrollData.getSalary(), employeePayrollData.getGender(), employeePayrollData.getStartDate(), employeePayrollData.getCompanyName(), employeePayrollData.getDepartments());
				System.out.println("Employee added: " + employeePayrollData.getName());
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
			employeeInsertionStatus.put(employeePayrollData.hashCode(), false);
			Runnable task = () -> {
				System.out.println("Employee Being added: " + Thread.currentThread().getName());
				try {
					this.addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getAddress(), 
							employeePayrollData.getSalary(), employeePayrollData.getGender(), employeePayrollData.getStartDate(),
							employeePayrollData.getCompanyName(), employeePayrollData.getDepartments());
				} catch (DatabaseException e) {
					System.out.println(e.getMessage());
				}
				employeeInsertionStatus.put(employeePayrollData.hashCode(), true);
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
	}
	
	public void addEmployeeToPayroll(EmployeePayrollData employeePayrollData, IOService ioService) {
		if(ioService.equals(IOService.DB_IO)) {
			try {
				addEmployeeToPayroll(employeePayrollData.getName(), employeePayrollData.getAddress(), 
						employeePayrollData.getSalary(), employeePayrollData.getGender(), employeePayrollData.getStartDate(), employeePayrollData.getCompanyName(), employeePayrollData.getDepartments());
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
		}
		else {
			employeePayrollDataList.add(employeePayrollData);
		}
	}

	/**
	 * To add Employee Payroll Data to Database;
	 */
	public int addEmployeeToPayroll(String name, String address, double salary, char gender, LocalDate startDate,
									String companyName, String ... departments) throws DatabaseException {
		EmployeePayrollData employeePayrollData = employeePayrollDBService.addEmployeePayrollToDB(name, address, salary, gender,
																									startDate, companyName, departments);
		employeePayrollDataList.add(employeePayrollData);
		return employeePayrollData.getId();
	}

	/**
	 * @param name
	 * @param salary
	 * @throws DatabaseException
	 * To Update Employee Salary
	 */
	public void updateEmployeeSalary(int id, double salary, IOService ioService) {
		if (ioService.equals(IOService.DB_IO)) {
			int result = 0;
			try {
				result = employeePayrollDBService.updateEmployeeSalary(id, salary);
			} catch (DatabaseException e) {
				System.out.println(e.getMessage());
			}
			if (result == 0) return;
		}
		EmployeePayrollData employeePayrollData = getEmployeePayrollData(id);
		if (employeePayrollData != null) employeePayrollData.setSalary(salary);
	}
	
	/**
	 * Update multiple employee salary using threads
	 */
	public boolean updateEmployeeSalary(Map<Integer, Double> newSalaries) {
		Map<Integer, Boolean> salaryUpdationStatus = new HashMap<Integer, Boolean>();
		Map<Integer, Boolean> syncStatus = new HashMap<Integer, Boolean>();
		newSalaries.entrySet().forEach(entry -> {
			int id = entry.getKey();
			double salary = entry.getValue();
			salaryUpdationStatus.put(id, false);
			Runnable task = () -> {
				System.out.println("Employee Being Updated Id: " + Thread.currentThread().getName());
				try {
					updateEmployeeSalary(id, salary, IOService.DB_IO);
					syncStatus.put(id, isEmployeePayrollInSyncWithDB(id));
				} catch (DatabaseException e) {
					System.out.println(e.getMessage());
				}
				salaryUpdationStatus.put(id, true);
				System.out.println("Employee Updated Id: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, String.valueOf(id));
			thread.start();
		});
		while (salaryUpdationStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		return (syncStatus.containsValue(false)) ? false : true;
	}

	/**
	 * Remove employee with given id from the DB
	 */
	public void removeEmployeeFromDB(int id) {
		try {
			employeePayrollDBService.removeEmployee(id);
			employeePayrollDataList = employeePayrollDataList.stream()
															 .filter(employeePayrollData -> employeePayrollData.getId() != id)
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
	public boolean isEmployeePayrollInSyncWithDB(int id) throws DatabaseException {
		EmployeePayrollData list = employeePayrollDBService.getEmployeeData(id);
		return list.equals(getEmployeePayrollData(id));
	}

	/**
	 * @param name
	 * @returns Employee Payroll Data Object with given employee name
	 */
	public EmployeePayrollData getEmployeePayrollData(int id) {
		return employeePayrollDataList.stream()
									  .filter(employeePayrollData -> employeePayrollData.getId() == id)
									  .findFirst()
									  .orElse(null);
	}

	/**
	 * Counts the entries
	 */
	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.REST_IO)) {
			return employeeDataSize();
		}
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
