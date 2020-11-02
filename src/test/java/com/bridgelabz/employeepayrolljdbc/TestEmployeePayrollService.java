package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.employeepayrolljdbc.EmployeePayrollDBService.statementType;

public class TestEmployeePayrollService {

	private EmployeePayrollService employeePayrollService;
	private List<EmployeePayroll> employeeList;

	@Before
	public void init() {
		employeePayrollService = new EmployeePayrollService();
	}

	// To test the retrieved entries from database
	@Test
	public void givenEmployeePayrollInDBWhenRetrievedShouldMatchEmployeeCount() {
		try {
			employeeList = employeePayrollService.readData();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertEquals(3, employeeList.size());
	}

	// To test whether database is updated for a given entry or not using statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedShouldSyncWithDatabase() {
		boolean result = false;
		try {
			employeePayrollService.updateData("Terisa", 5000000.00, statementType.STATEMENT);
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Terisa");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

	// To test whether database is updated for a given entry or not using prepared
	// statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedUsingPreparedStatementShouldSyncWithDatabase() {
		boolean result = false;
		try {
			employeePayrollService.updateData("Terisa", 3000000.00, statementType.STATEMENT);
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Terisa");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

	// To test the data retrieved when start date of employee given
	@Test
	public void givenDateRangeWhenRetrievedEmployeeDataShouldMatchEmployeeCount() {
		try {
			employeeList = employeePayrollService.getEmployeeDataByDate(LocalDate.of(2018, 02, 01), LocalDate.now());
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertEquals(2, employeeList.size());
	}

	// To test the retrieved sum of salaries by gender
	@Test
	public void givenEmployeePayrollDBWhenRetrievedSumOfSalaryByGenderShouldAssertEquals() {
		Map<String, Double> salarySumByGender = null;
		try {
			salarySumByGender = employeePayrollService.getSalarySumByGender();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		Double maleSalary = 4000000.00;
		assertEquals(maleSalary, salarySumByGender.get("M"));
	}

	// To test when a new employee is added to database
	@Test
	public void givenNewEmployeeWhenAddedShouldSyncWithDatabase() {
		boolean result = false;
		try {
			employeePayrollService.addEmployeeData("Mark", 'M', 4000000.00, LocalDate.now());
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Mark");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

	// To populate employee and payroll tables simultaneously
	@Test
	public void givenNewEmployeeWhenAddedShouldPopulatePayrollTable() {
		boolean result = false;
		try {
			employeePayrollService.addEmployeeToEmployeeAndPayroll("Rachel", 'F', 3000000.00, LocalDate.now());
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Rachel");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

	// To populate employee and payroll tables simultaneously
	@Test
	public void givenNewEmployeeWhenAddedShouldPopulateAllRelatedTables() {
		boolean result = false;
		EmployeePayroll employee = new EmployeePayroll("Phoebe", 1, "7098671234", "Telangana", 'F', LocalDate.now(),
				2500000, new int[] { 51, 52 });
		try {
			employeePayrollService.addEmployeeToAllRelatedTables(employee);
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase(employee.getName());
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

}
