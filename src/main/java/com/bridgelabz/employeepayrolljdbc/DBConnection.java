package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.bridgelabz.employeepayrolljdbc.DatabaseException.exceptionType;

public class DBConnection {
	private static int connectionCounter = 0;

	// To load drivers and establish connection
	public static Connection getConnection() throws DatabaseException {
		connectionCounter++;
		Connection connection = null;
		String jdbcDriver = "com.mysql.jdbc.Driver";
		String dataBaseURL = "jdbc:mysql://localhost:3306/payroll_service";

		// Load driver class
		try {
			Class.forName(jdbcDriver);
			EmployeePayrollService.LOG.info("Driver Loaded!!!");
		} catch (ClassNotFoundException e) {
			throw new DatabaseException("Unable to load the driver!!", exceptionType.DRIVER_CONNECTION);
		}

		EmployeePayrollService.LOG.info("Processing Thread: " + Thread.currentThread().getName()
				+ " Connecting to database with connectionId: " + connectionCounter);

		// Establish Connection
		try {
			connection = DriverManager.getConnection(dataBaseURL, "root", "Charani@19");
		} catch (SQLException e) {
			throw new DatabaseException("Unable to connect to database!!", exceptionType.DATABASE_CONNECTION);
		}

		EmployeePayrollService.LOG.info("Processing Thread: " + Thread.currentThread().getName() + " ConnectionId : "
				+ connectionCounter + " Connection established successfully!!!");
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
