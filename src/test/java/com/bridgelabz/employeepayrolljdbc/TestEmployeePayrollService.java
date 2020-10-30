package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
		employeeList = employeePayrollService.readData();
		assertEquals(3, employeeList.size());
	}

	// To test whether database is updated for a given entry or not using statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedShouldSyncWithDatabase() {
		employeeList = employeePayrollService.readData();
		employeePayrollService.updateData("Terisa", 3000000.00, EmployeePayrollService.statementType.STATEMENT);
		boolean result = employeePayrollService.check(employeeList, "Terisa", 3000000.00);
		assertTrue(result);
	}

	// To test whether database is updated for a given entry or not using prepared statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedUsingPreparedStatementShouldSyncWithDatabase() {
		employeeList = employeePayrollService.readData();
		employeePayrollService.updateData("Terisa", 2000000.00, EmployeePayrollService.statementType.PREPARED_STATEMENT);
		boolean result = employeePayrollService.check(employeeList, "Terisa", 2000000.00);
		assertTrue(result);
	}

}
