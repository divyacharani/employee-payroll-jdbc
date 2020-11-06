package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.bridgelabz.employeepayrolljdbc.EmployeePayrollDBService.statementType;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestEmployeePayrollJsonRestAssure {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	private EmployeePayroll[] getEmployeeList() {
		Response response = RestAssured.get("/employees");
		EmployeePayrollService.LOG.info("Employee payroll entries in JSON Server :\n" + response.asString());
		EmployeePayroll[] arrayOfEmployees = new Gson().fromJson(response.asString(), EmployeePayroll[].class);
		return arrayOfEmployees;
	}

	private Response addEmployeeToJsonServer(EmployeePayroll employeePayroll) {
		String empJson = new Gson().toJson(employeePayroll);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}

	@Test
	public void UC1givenNewEmployeeWhenAddedShouldMatchResponseCode() {
		EmployeePayrollService employeePayrollService;
		EmployeePayroll[] arrayOfEmployees = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		EmployeePayroll employeePayroll = new EmployeePayroll(0, "Mark", 4000000.00, LocalDate.now());
		Response response = addEmployeeToJsonServer(employeePayroll);
		int statusCode = response.getStatusCode();
		assertEquals(201, statusCode);
		employeePayroll = new Gson().fromJson(response.asString(), EmployeePayroll.class);
		employeePayrollService.addEmployeePayroll(employeePayroll);
		long entries = employeePayrollService.countEntries();
		assertEquals(5, entries);
	}

	@Test
	public void UC2givenListOfEmployeeWhenAddedShouldMatchResponseCode() {
		EmployeePayrollService employeePayrollService;
		EmployeePayroll[] arrayOfEmployees = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		List<EmployeePayroll> employeePayrollList = new ArrayList<>();
		employeePayrollList.add(new EmployeePayroll(0, "Rachel", 1500000.00, LocalDate.now()));
		employeePayrollList.add(new EmployeePayroll(0, "Monica", 2000000.00, LocalDate.now()));
		employeePayrollList.add(new EmployeePayroll(0, "Joey", 3500000.00, LocalDate.now()));
		for (EmployeePayroll employee : employeePayrollList) {
			Response response = addEmployeeToJsonServer(employee);
			int statusCode = response.getStatusCode();
			assertEquals(201, statusCode);
			employee = new Gson().fromJson(response.asString(), EmployeePayroll.class);
			employeePayrollService.addEmployeePayroll(employee);
		}
		long entries = employeePayrollService.countEntries();
		assertEquals(8, entries);
	}
	
	@Test
	public void UC3givenUpdatedSalaryWhenUpdatedShouldMatchResponseCode() {
		EmployeePayrollService employeePayrollService;
		EmployeePayroll[] arrayOfEmployees = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		employeePayrollService.updateEmployeeList("Chandler", 5000000.00);
		EmployeePayroll employee = employeePayrollService.getEmployee("Chandler");
		String empJson = new Gson().toJson(employee);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		Response response = request.put("/employees/" + employee.getEmployeeId());
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);
	}

}
