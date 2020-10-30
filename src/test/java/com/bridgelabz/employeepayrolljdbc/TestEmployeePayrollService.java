package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.employeepayrolljdbc.EmployeePayrollService.statementType;

public class TestEmployeePayrollService {

	private EmployeePayrollService employeePayrollService;
	private List<EmployeePayroll> employeeList;

	@Before
	public void init() {
		employeePayrollService = EmployeePayrollService.getInstance();
	}

	// To test the retrieved entries from database
	@Test
	public void givenEmployeePayrollInDBWhenRetrievedShouldMatchEmployeeCount() throws DatabaseException {
		employeeList = employeePayrollService.readData();
		assertEquals(3, employeeList.size());
	}

	// To test whether database is updated for a given entry or not using statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedShouldSyncWithDatabase() throws DatabaseException {
		employeeList = employeePayrollService.readData();
		employeePayrollService.updateData("Terisa", 3000000.00, statementType.STATEMENT);
		boolean result = employeePayrollService.check(employeeList, "Terisa", 3000000.00);
		assertTrue(result);
	}

	// To test whether database is updated for a given entry or not using prepared
	// statement
	@Test
	public void givenUpdatedSalaryWhenUpdatedUsingPreparedStatementShouldSyncWithDatabase() throws DatabaseException {
		employeeList = employeePayrollService.readData();
		employeePayrollService.updateData("Terisa", 2000000.00, statementType.PREPARED_STATEMENT);
		boolean result = employeePayrollService.check(employeeList, "Terisa", 2000000.00);
		assertTrue(result);
	}

	// To test the data retrieved when start date of employee given
	@Test
	public void givenDateRangeWhenRetrievedEmployeeDataShouldMatchEmployeeCount() throws DatabaseException {
		employeeList = employeePayrollService.getEmployeeDataByDate(LocalDate.of(2018, 01, 01), LocalDate.now());
		assertEquals(3, employeeList.size());
	}

}
