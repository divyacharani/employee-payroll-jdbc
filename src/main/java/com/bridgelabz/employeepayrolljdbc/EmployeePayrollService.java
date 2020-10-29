package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmployeePayrollService {

	public static final Logger LOG = LogManager.getLogger(EmployeePayrollService.class);

	public static void main(String[] args) {
		// Welcome Message
		LOG.info("Welcome to Employee Payroll Service");

	}

	// To read payroll Data from database
	public List<EmployeePayroll> readData() {
		String sqlQuery = "SELECT * FROM employee_payroll";
		List<EmployeePayroll> employeePayrollList = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("startDate").toLocalDate();
				employeePayrollList.add(new EmployeePayroll(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

}
