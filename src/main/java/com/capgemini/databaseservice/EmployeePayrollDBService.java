package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.capgemini.exceptions.DatabaseException;
import com.capgemini.exceptions.DatabaseException.ExceptionType;
import com.capgemini.payrolldata.EmployeePayrollData;

public class EmployeePayrollDBService {
	
	private PreparedStatement employeePayrollDataPreparedStatement;
	private static EmployeePayrollDBService employeePayrollDBService;
	
	private EmployeePayrollDBService() {
		
	}
	
	public static EmployeePayrollDBService createInstance() {
		if(employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	/**
	 * @return
	 * @throws DatabaseException
	 * Returns employee data from the DB
	 */
	public ArrayList<EmployeePayrollData> readEmployeeData() throws DatabaseException {
		String query = "SELECT * FROM employee_payroll";
		return getEmployeePayrollData(query);
	}
	
	/**
	 * @param name
	 * @return list of employee data with given name
	 * @throws DatabaseException
	 * @throws  
	 */
	public ArrayList<EmployeePayrollData> getEmployeeData(String name) throws DatabaseException {
		if(employeePayrollDataPreparedStatement == null) {
			getPreparedStatementForEmployeeData();
		}
		try {
			employeePayrollDataPreparedStatement.setString(1, name);
			ResultSet result = employeePayrollDataPreparedStatement.executeQuery();
			return getEmployeePayrollData(result);
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}
	

	/**
	 * @param startDate
	 * @param endDate
	 * @return Employee Payroll Data in given date range
	 * @throws DatabaseException
	 */
	public ArrayList<EmployeePayrollData> getEmployeeDataForDateRange(LocalDate startDate, LocalDate endDate) throws DatabaseException {
		String query = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';", 
										Date.valueOf(startDate), Date.valueOf(endDate));
		
		return getEmployeePayrollData(query);
	}
	

	/**
	 * @return sum of salaries against gender
	 * @throws DatabaseException
	 */
	public Map<String, Double> getSumOfSalariesByGender() throws DatabaseException {
		String query = "SELECT gender, SUM(salary) as salary FROM employee_payroll GROUP BY gender";
		return performOperationsOnsalaryByGender(query);
	}
	
	/**
	 * @return average salary by gender
	 * @throws DatabaseException
	 */
	public Map<String, Double> getAvgSalaryByGender() throws DatabaseException {
		String query = "SELECT gender, AVG(salary) as salary FROM employee_payroll GROUP BY gender";
		return performOperationsOnsalaryByGender(query);
	}
	

	/**
	 * @return minimum salary by gender
	 * @throws DatabaseException
	 */
	public Map<String, Double> getMinSalaryByGender() throws DatabaseException {
		String query = "SELECT gender, MIN(salary) as salary FROM employee_payroll GROUP BY gender";
		return performOperationsOnsalaryByGender(query);
	}

	/**
	 * @return maximum salary by gender
	 * @throws DatabaseException
	 */
	public Map<String, Double> getMaxSalaryByGender() throws DatabaseException {
		String query = "SELECT gender, MAX(salary) as salary FROM employee_payroll GROUP BY gender";
		return performOperationsOnsalaryByGender(query);
	}
	
	/**
	 * @return employee count by gender
	 * @throws DatabaseException
	 */
	public Map<String, Integer> getCountByGender() throws DatabaseException {
		String query = "SELECT gender, Count(salary) as count FROM employee_payroll GROUP BY gender";
		Map<String, Integer> map = new HashMap<String, Integer>();
		try(Connection connection = getConnection();){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			while(result.next()) {
				map.put(result.getString("gender"), result.getInt("count"));
			}
			return map;
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}

	/**
	 * @param name
	 * @param salary
	 * @return execute update output
	 * @throws DatabaseException
	 */
	public int updateEmployeeSalary(String name, double salary) throws DatabaseException {
		String query = String.format("update employee_payroll set salary = %.2f where name = '%s'", salary, name);
		try (Connection connection = getConnection();) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(query);
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}
	
	/**
	 * @param name
	 * @param salary
	 * @throws DatabaseException
	 * updates the employee salary using prepared statement
	 */
	public int updateEmployeeSalaryUsingPreparedStatement(String name, double salary) throws DatabaseException {
		String query = String.format("update employee_payroll set salary = ? where name = ?");
		try (Connection connection = getConnection();) {
			PreparedStatement prepareStatement = connection.prepareStatement(query);
			prepareStatement.setString(2, name);
			prepareStatement.setDouble(1, salary);
			return prepareStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}
	
	/**
	 * @returns Connection Object
	 * @throws DatabaseException
	 * 
	 */
	private Connection getConnection() throws DatabaseException {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service";
		String user = "root";
		String password = "Gratitudelog1";
		Connection connection;
		try {
			connection = DriverManager.getConnection(jdbcURL, user, password);
			System.out.println("Connection successfully established!" + connection);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to connect to the database", ExceptionType.UNABLE_TO_CONNECT);
		}
		return connection;
	}

	/**
	 * @param result
	 * @return List of Employee Payroll Data object using Result set
	 * @throws DatabaseException
	 */
	private ArrayList<EmployeePayrollData> getEmployeePayrollData(ResultSet result) throws DatabaseException {
		ArrayList<EmployeePayrollData> list = new ArrayList<EmployeePayrollData>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				Double salary = result.getDouble("salary");
				LocalDate date = result.getDate("start").toLocalDate();
				list.add(new EmployeePayrollData(id, name, salary, date));
			}
			return list;
		} catch (SQLException e) {
			throw new DatabaseException("Error in retriving data", ExceptionType.UNABLE_TO_RETRIEVE_DATA);
		}
	}

	/**
	 * @throws DatabaseException
	 * Sets the prepared statement to view employee data for a given name
	 */
	private void getPreparedStatementForEmployeeData() throws DatabaseException {
		Connection connection = getConnection();
		String query = "select * from employee_payroll where name = ?";
		try {
			employeePayrollDataPreparedStatement = connection.prepareStatement(query);
		} catch (SQLException e) {
			throw new DatabaseException("Error while getting prepared statement", ExceptionType.UNABLE_TO_GET_PREPARED_STATEMENT);
		}
	}
	
	/**
	 * @param query
	 * @return Employee payroll data based on given query
	 * @throws DatabaseException
	 */
	private ArrayList<EmployeePayrollData> getEmployeePayrollData(String query) throws DatabaseException {
		try (Connection connection = getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			return getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}
	
	/**
	 * @param query
	 * Perform Database operation such as Sum, avg, min, max, count and return the result
	 * @throws DatabaseException
	 */
	private Map<String, Double> performOperationsOnsalaryByGender(String query) throws DatabaseException {
		Map<String, Double> map = new HashMap<String, Double>();
		try(Connection connection = getConnection();){
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			while(result.next()) {
				map.put(result.getString("gender"), result.getDouble("salary"));
			}
			return map;
		} catch (SQLException e) {
			throw new DatabaseException("Error while executing the query", ExceptionType.UNABLE_TO_EXECUTE_QUERY);
		}
	}
}
