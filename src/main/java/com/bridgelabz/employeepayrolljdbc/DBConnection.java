package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DBConnection {

	// To load drivers and establish connection
	public static Connection getConnection() {
		Connection connection = null;
		String jdbcDriver = "com.mysql.jdbc.Driver";
		String dataBaseURL = "jdbc:mysql://localhost:3306/payroll_service";

		// Load driver class
		try {
			Class.forName(jdbcDriver);
			EmployeePayrollService.LOG.info("Driver Loaded!!!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Get list of drivers
		listDrivers();

		EmployeePayrollService.LOG.info("Connecting to....." + dataBaseURL);

		// Establish Connection
		try {
			connection = DriverManager.getConnection(dataBaseURL, "root", "Charani@19");
			EmployeePayrollService.LOG.info("Connection established successfully!!!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	// To get list of drivers
	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		EmployeePayrollService.LOG.info("Drivers List : ");
		while (driverList.hasMoreElements()) {
			Driver driverClass = driverList.nextElement();
			EmployeePayrollService.LOG.info(driverClass.getClass().getName());
		}
	}
}
