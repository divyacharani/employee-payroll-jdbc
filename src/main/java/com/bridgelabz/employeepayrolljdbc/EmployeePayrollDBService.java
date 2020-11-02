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
		String sqlQuery = "SELECT employee_id, name, salary, start_date FROM employee WHERE is_active = true;";
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
				employeePayrollList.add(new EmployeePayroll(result.getInt("employee_id"), result.getString("name"),
						result.getDouble("salary"), result.getDate("start_date").toLocalDate()));
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
		String sqlQuery = String.format("UPDATE employee SET salary = %.2f WHERE NAME = '%s' AND is_active = true;",
				salary, name);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sqlQuery);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	private int updateUsingPreparedStatement(String name, double salary) throws DatabaseException {
		String sql = "UPDATE employee SET salary = ? WHERE name = ? AND is_active = true;";
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
		String sql = "SELECT employee_id, name, salary, start_date FROM employee WHERE name = ? AND is_active = true;";
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
		String sqlQuery = String.format(
				"SELECT employee_id, name, salary, start_date FROM employee WHERE start_date BETWEEN '%s' AND '%s' AND is_active = true;",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return executeStatementQuery(sqlQuery);
	}

	// To get sum of salaries of male and female employees
	public Map<String, Double> getSalarySumByGenderDB() throws DatabaseException {
		Map<String, Double> salarySumByGender = new HashMap<>();
		String sqlQuery = "SELECT gender, SUM(salary) AS salary_sum FROM employee WHERE is_active = true GROUP BY gender ;";
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
		return new DBServiceAddDetails().addEmployeeDataDBService(name, gender, salary, startDate);
	}

	// To add new employee to both employee and payroll tables
	public EmployeePayroll addEmployeeToEmployeeAndPayrollDB(String name, char gender, double salary,
			LocalDate startDate) throws DatabaseException {
		return new DBServiceAddDetails().addEmployeeToEmployeeAndPayrollDBService(name, gender, salary, startDate);
	}

	public EmployeePayroll addEmployeeToAllRelatedTablesDB(EmployeePayroll employeePayroll) throws DatabaseException {
		return new DBServiceAddDetails().addEmployeeToAllRelatedTablesDBService(employeePayroll);
	}

	// To remove employee from payroll
	public int removeEmployeeFromPayrollTableDB(String name) throws DatabaseException {
		int employeeId = getEmployeeDataByNameDB(name).get(0).getEmployeeId();
		int rowAffected = 0;
		String sqlQuery = String.format("DELETE FROM payroll WHERE employee_id = '%s';", employeeId);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			rowAffected = statement.executeUpdate(sqlQuery);
			if (rowAffected == 1) {
				updateActiveStatusOfEmployee(employeeId);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
		return rowAffected;
	}

	private void updateActiveStatusOfEmployee(int employeeId) throws DatabaseException {
		String sqlQuery = String.format("UPDATE employee SET is_active = false WHERE employee_id = '%s';", employeeId);
		try (Connection connection = DBConnection.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sqlQuery);
		} catch (SQLException e) {
			throw new DatabaseException("Unable to execute query!!", exceptionType.EXECUTE_QUERY);
		}
	}

	public boolean checkActiveStatusDB(String name) throws DatabaseException {
		String sqlQuery = String.format("SELECT employee_id, name, salary, start_date FROM employee WHERE name = '%s';", name);
		boolean status = executeStatementQuery(sqlQuery).get(0).isActive();
		return status;
	}
}
