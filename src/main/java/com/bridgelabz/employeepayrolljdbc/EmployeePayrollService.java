package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmployeePayrollService {

	private static final Logger LOG = LogManager.getLogger(EmployeePayrollService.class);

	public static void main(String[] args) {
		// Welcome Message
		LOG.info("Welcome to Employee Payroll Service");

		getConnection();

	}

	// To load drivers and establish connection
	private static void getConnection() {
		Connection connection;
		String jdbcDriver = "com.mysql.jdbc.Driver";
		String dataBaseURL = "jdbc:mysql://localhost:3306/address_book_service";

		// Load driver class
		try {
			Class.forName(jdbcDriver);
			LOG.info("Driver Loaded!!!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Get list of drivers
		listDrivers();

		LOG.info("Connecting to....." + dataBaseURL);

		// Establish Connection
		try {
			connection = DriverManager.getConnection(dataBaseURL, "root", "Charani@19");
			LOG.info("Connection established successfully!!!");
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// To get list of drivers
	private static void listDrivers() {
		Enumeration<Driver> driverList = DriverManager.getDrivers();
		LOG.info("Drivers List : ");
		while (driverList.hasMoreElements()) {
			Driver driverClass = driverList.nextElement();
			LOG.info(driverClass.getClass().getName());
		}

	}

}
