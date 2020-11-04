package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestEmployeePayrollServiceWithThreads {

	private EmployeePayrollService employeePayrollService;
	private List<EmployeePayroll> employeeList;

	@Before
	public void init() {
		employeePayrollService = new EmployeePayrollService();
	}

	@Test
	public void givenListOfEmployeesWhenAddedShouldMatchNoOfEntries() {
		List<EmployeePayroll> employeeList = new ArrayList<>();
		employeeList.add(new EmployeePayroll("Rachel", 'F', 3000000.00, LocalDate.now()));
		employeeList.add(new EmployeePayroll("Mark", 'M', 2000000.00, LocalDate.now()));
		employeeList.add(new EmployeePayroll("Terisa", 'F', 4000000.00, LocalDate.now()));
		employeeList.add(new EmployeePayroll("Charlie", 'M', 2500000.00, LocalDate.now()));
		employeeList.add(new EmployeePayroll("Chandler", 'M', 4500000.00, LocalDate.now()));
		Instant start = null;
		Instant end = null;
		Instant startThread = null;
		Instant endThread = null;
		try {
			start = Instant.now();
			employeePayrollService.addEmployeeListToEmployeeAndPayrollTable(employeeList);
			end = Instant.now();
			startThread = Instant.now();
			employeePayrollService.addEmployeeListToEmployeeAndPayrollWithThreads(employeeList);
			endThread = Instant.now();
			employeeList = employeePayrollService.readData();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		EmployeePayrollService.LOG.info("Duration without Threads : " + Duration.between(start, end));
		EmployeePayrollService.LOG.info("Duration with Threads : " + Duration.between(startThread, endThread));
		
		assertEquals(10, employeeList.size());
	}
	
	@Test
	public void givenListOfUpdatedSalaryWhenUpdatedShouldSyncWithDatabase() {
		Map<String,Double> nameToUpdatedSalary = new HashMap<>();
		nameToUpdatedSalary.put("Rachel", 3500000.00);
		nameToUpdatedSalary.put("Mark", 2500000.00);
		nameToUpdatedSalary.put("Charlie", 3000000.00);
		boolean result = false;
		Instant startThread = null;
		Instant endThread = null;
		try {
			startThread = Instant.now();
			employeePayrollService.updateSalaryList(nameToUpdatedSalary);
			endThread = Instant.now();
			result = employeePayrollService.checkEmployeeDataInSyncWithDatabase("Mark");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		EmployeePayrollService.LOG.info("Duration with Threads : " + Duration.between(startThread, endThread));
		assertTrue(result);
	}
}
