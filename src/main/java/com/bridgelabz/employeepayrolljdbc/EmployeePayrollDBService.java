package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bridgelabz.employeepayrolljdbc.DatabaseException.exceptionType;

public class EmployeePayrollDBService {

	private static EmployeePayrollDBService employeePayrollDBService;
	List<EmployeePayroll> employeePayrollList = new ArrayList<>();
	PreparedStatement preparedStatementByName;

	private EmployeePayrollDBService() {
	}

	public enum statementType {
		STATEMENT, PREPARED_STATEMENT
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	// To read payroll Data from database
	public List<EmployeePayroll> readDataDB() throws DatabaseException {
		String sqlQuery = "SELECT * FROM employee_payroll;";
		return executeStatementQuery(sqlQuery);
	}

	private List<EmployeePayroll> executeStatementQuery(String sqlQuery) throws DatabaseException {
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

	private List<EmployeePayroll> getResultSet(ResultSet result) throws DatabaseException {
		List<EmployeePayroll> employeePayrollList = new ArrayList<>();
		try {
			while (result.next()) {
				employeePayrollList.add(new EmployeePayroll(result.getInt("id"), result.getString("name"),
						result.getDouble("salary"), result.getDate("startDate").toLocalDate()));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return employeePayrollList;
	}

	// To update data in the database
	public int updateDataDB(String name, double salary, statementType type) throws DatabaseException {
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

	// To get employee data by employee name
	public List<EmployeePayroll> getEmployeeDataByNameDB(String name) throws DatabaseException {
		List<EmployeePayroll> employeePayrollListByName = null;
		if (preparedStatementByName == null)
			preparedStatementToGetEmployeeDataByName();
		ResultSet result = null;
		try {
			preparedStatementByName.setString(1, name);
			result = preparedStatementByName.executeQuery();
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		employeePayrollListByName = getResultSet(result);
		return employeePayrollListByName;
	}

	// Prepared statement for employee payroll data
	private void preparedStatementToGetEmployeeDataByName() throws DatabaseException {
		String sql = "SELECT * FROM employee_payroll WHERE name = ?;";
		try {
			Connection connection = DBConnection.getConnection();
			preparedStatementByName = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	// To get employee data joined after a particular date
	public List<EmployeePayroll> getEmployeeDataByDateDB(LocalDate startDate, LocalDate endDate)
			throws DatabaseException {
		String sqlQuery = String.format("SELECT * FROM employee_payroll WHERE startDate BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return executeStatementQuery(sqlQuery);
	}

	// To get sum of salaries of male and female employees
	public Map<String, Double> getSalarySumByGenderDB() throws DatabaseException {
		Map<String, Double> salarySumByGender = new HashMap<>();
		String sqlQuery = "SELECT gender, SUM(salary) AS salary_sum FROM employee_payroll GROUP BY gender;";
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sqlQuery);
			while (result.next()) {
				salarySumByGender.put(result.getString("gender"), result.getDouble("salary_sum"));
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return salarySumByGender;
	}

	// To add new employee to database
	public EmployeePayroll addEmployeeDataDB(String name, char gender, double salary, LocalDate startDate)
			throws DatabaseException {

		int employeeId = -1;
		EmployeePayroll employeePayrollData = null;
		String sqlQuery = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,startDate) VALUES('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sqlQuery, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next())
					employeeId = result.getInt(1);
			}
			employeePayrollData = new EmployeePayroll(employeeId, name, gender, salary, startDate);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return employeePayrollData;
	}

	// To add new employee to both employee and payroll tables
	public EmployeePayroll addEmployeeToEmployeeAndPayrollDB(String name, char gender, double salary, LocalDate startDate)
			throws DatabaseException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayroll employeePayrollData = null;
		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll(name,gender,salary,startDate) VALUES ('%s','%s','%s','%s')", name,
					gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"INSERT INTO payroll_details(employee_id,basic_pay,deductions,taxable_pay,tax ,net_pay)VALUES (%s,%s,%s,%s,%s,%s)",
					employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayroll(employeeId, name, salary, startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return employeePayrollData;
	}
}
