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
			employeePayrollService.updateData("Terisa", 4000000.00, statementType.STATEMENT);
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
			employeePayrollService.updateData("Terisa", 2000000.00, statementType.STATEMENT);
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
			employeeList = employeePayrollService.getEmployeeDataByDate(LocalDate.of(2019, 01, 01), LocalDate.now());
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
		Double femaleSalary = 2000000.00;
		assertEquals(femaleSalary, salarySumByGender.get("F"));
	}

	// To test when a new employee is added to database

	@Test
	public void givenNewEmployeeWhenAddedShouldSyncWithDatabase() {
		boolean result = false;
		try {
			employeePayrollService.addEmployeeData("Charlie", 'M', 4000000.00, LocalDate.now());
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Charlie");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		assertTrue(result);
	}

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

}
