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

	@Test
	public void givenEmployeePayrollInDB_WhenRetrievedShouldMatchEmployeeCount() {
		employeeList = employeePayrollService.readData();
		assertEquals(3, employeeList.size());
	}

}
