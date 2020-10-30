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

import com.bridgelabz.employeepayrolljdbc.DatabaseException.exceptionType;

public class EmployeePayrollService {

	public static final Logger LOG = LogManager.getLogger(EmployeePayrollService.class);
	private static EmployeePayrollService employeePayrollService;
	PreparedStatement preparedStatement;

	private EmployeePayrollService() {

	}

	// To get instance for EmployeePayrollService
	public static EmployeePayrollService getInstance() {
		if (employeePayrollService == null)
			employeePayrollService = new EmployeePayrollService();
		return employeePayrollService;
	}

	public enum statementType {
		STATEMENT, PREPARED_STATEMENT
	}

	public static void main(String[] args) {
		// Welcome Message
		LOG.info("Welcome to Employee Payroll Service");

	}

	// To read payroll Data from database
	public List<EmployeePayroll> readData() throws DatabaseException {
		String sqlQuery = "SELECT * FROM employee_payroll";
		List<EmployeePayroll> employeePayrollList = null;
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			employeePayrollList = getResultSet(result);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return employeePayrollList;
	}

	// To get employee data by employee name
	public List<EmployeePayroll> getEmployeeDataByName(String name) throws DatabaseException {
		List<EmployeePayroll> employeePayrollListByName = null;
		if (preparedStatement == null)
			preparedStatemenToGetEmployeeDataByName();
		ResultSet result = null;
		try {
			preparedStatement.setString(1, name);
			result = preparedStatement.executeQuery();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		employeePayrollListByName = getResultSet(result);
		return employeePayrollListByName;
	}

	private List<EmployeePayroll> getResultSet(ResultSet result) throws DatabaseException {
		List<EmployeePayroll> employeePayrollList = new ArrayList<>();
		try {
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				double salary = result.getDouble("salary");
				LocalDate startDate = result.getDate("startDate").toLocalDate();
				employeePayrollList.add(new EmployeePayroll(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return employeePayrollList;
	}

	// To update data in the database
	public int updateData(String name, double salary, statementType type) throws DatabaseException {
		if (type.equals(statementType.STATEMENT))
			return updateUsingStatement(name, salary);
		if (type.equals(statementType.PREPARED_STATEMENT))
			return updateUsingPreparedStatement(name, salary);
		return 0;
	}

	private int updateUsingStatement(String name, double salary) throws DatabaseException {
		String sqlQuery = String.format("UPDATE employee_payroll SET salary = %.2f WHERE NAME = '%s';", salary, name);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sqlQuery);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	private int updateUsingPreparedStatement(String name, double salary) throws DatabaseException {
		String sql = "UPDATE employee_payroll SET salary = ? WHERE name = ?";
		try (Connection connection = DBConnection.getConnection()) {
			PreparedStatement preparedStatementUpdate = connection.prepareStatement(sql);
			preparedStatementUpdate.setDouble(1, salary);
			preparedStatementUpdate.setString(2, name);
			return preparedStatementUpdate.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	// To check the database after updating
	public boolean check(List<EmployeePayroll> employeePayrollList, String name, double salary)
			throws DatabaseException {
		EmployeePayroll employeeObj = getEmployee(employeePayrollList, name);
		employeeObj.setSalary(salary);
		return employeeObj.equals(getEmployee(readData(), name));
	}

	private EmployeePayroll getEmployee(List<EmployeePayroll> employeeList, String name) {
		EmployeePayroll employee = employeeList.stream().filter(employeeObj -> ((employeeObj.getName()).equals(name)))
				.findFirst().orElse(null);
		return employee;
	}

	// Prepared statement for employee payroll data
	private void preparedStatemenToGetEmployeeDataByName() throws DatabaseException {
		String sql = "SELECT * FROM employee_payroll WHERE name = ?";
		try (Connection connection = DBConnection.getConnection()) {
			preparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}
}
