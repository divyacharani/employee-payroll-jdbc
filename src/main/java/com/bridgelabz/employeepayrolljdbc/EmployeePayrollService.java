package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

	public enum statementType {
		STATEMENT, PREPARED_STATEMENT
	}

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

	// To update data in the database
	public int updateData(String name, double salary, statementType type) {
		if (type.equals(statementType.STATEMENT))
			return updateUsingStatement(name, salary);
		if (type.equals(statementType.PREPARED_STATEMENT))
			return updateUsingPreparedStatement(name, salary);
		return 0;
	}

	private int updateUsingStatement(String name, double salary) {
		String sqlQuery = String.format("UPDATE employee_payroll SET salary = %.2f WHERE NAME = '%s';", salary, name);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sqlQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int updateUsingPreparedStatement(String name, double salary) {
		String sql = "UPDATE employee_payroll SET salary = ? WHERE name = ?";
		try (Connection connection = DBConnection.getConnection()) {
			PreparedStatement preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setDouble(1, salary);
			preparedStatementUpdate.setString(2, name);
			return preparedStatementUpdate.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// To check the database after updating
	public boolean check(List<EmployeePayroll> employeePayrollList, String name, double salary) {
		EmployeePayroll employeeObj = getEmployee(employeePayrollList, name);
		employeeObj.setSalary(salary);
		return employeeObj.equals(getEmployee(readData(), name));
	}

	private EmployeePayroll getEmployee(List<EmployeePayroll> employeeList, String name) {
		EmployeePayroll employee = employeeList.stream().filter(employeeObj -> ((employeeObj.getName()).equals(name)))
				.findFirst().orElse(null);
		return employee;
	}

}
