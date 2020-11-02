package com.bridgelabz.employeepayrolljdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.bridgelabz.employeepayrolljdbc.DatabaseException.exceptionType;

public class DBServiceAddDetails {

	// To add new employee to database
	public EmployeePayroll addEmployeeDataDBService(String name, char gender, double salary, LocalDate startDate)
			throws DatabaseException {

		int employeeId = -1;
		EmployeePayroll employeePayrollData = null;
		String sqlQuery = String.format(
				"INSERT INTO employee (name,gender,salary,start_date) VALUES('%s','%s','%s','%s');", name, gender,
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
	public EmployeePayroll addEmployeeToEmployeeAndPayrollDBService(String name, char gender, double salary,
			LocalDate startDate) throws DatabaseException {
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
					"INSERT INTO employee(name,gender,salary,start_date) VALUES ('%s','%s','%s','%s');", name, gender,
					salary, Date.valueOf(startDate));
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
					"INSERT INTO payroll(employee_id,basic_pay,deductions,taxable_pay,income_tax ,net_pay)VALUES (%s,%s,%s,%s,%s,%s);",
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

	public EmployeePayroll addEmployeeToAllRelatedTablesDBService(EmployeePayroll employeePayroll) throws DatabaseException {
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
					"INSERT INTO employee(name,gender,salary,start_date,company_id,phone_number,address) VALUES ('%s','%s','%s','%s','%s','%s','%s');",
					employeePayroll.getName(), employeePayroll.getGender(), employeePayroll.getSalary(),
					Date.valueOf(employeePayroll.getStartDate()), employeePayroll.getCompanyId(),
					employeePayroll.getPhoneNumber(), employeePayroll.getAddress());
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
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			for (int departmentId : employeePayroll.getDepartmentId()) {
				String sql = String.format(
						"INSERT INTO employee_department (employee_id,department_id) VALUES ('%s', '%s');", employeeId,
						departmentId);
				int rowAffected = statement.executeUpdate(sql);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		try (Statement statement = connection.createStatement()) {
			double deductions = employeePayroll.getSalary() * 0.2;
			double taxablePay = employeePayroll.getSalary() - deductions;
			double tax = taxablePay * 0.1;
			double netPay = employeePayroll.getSalary() - tax;
			String sql = String.format(
					"INSERT INTO payroll(employee_id,basic_pay,deductions,taxable_pay,income_tax ,net_pay)VALUES (%s,%s,%s,%s,%s,%s);",
					employeeId, employeePayroll.getSalary(), deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayroll(employeeId, employeePayroll.getName(),
						employeePayroll.getSalary(), employeePayroll.getStartDate());
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
