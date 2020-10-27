package com.capgemini.databaseservice;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DBDemo {
	
	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service";
		String user = "root";
		String password = "Gratitudelog1";
		Connection connection;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver loaded successfully!");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Driver Missing!", e);
		}
		
		listDrivers();
		
		try {
			connection = DriverManager.getConnection(jdbcURL, user, password);
			System.out.println("Connection successfully established!" + connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		while(driverList.hasMoreElements()) {
			Driver driver = driverList.nextElement();
			System.out.println("   "+driver.getClass().getName());
		}
		
	}
}
