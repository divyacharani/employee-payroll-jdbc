package com.bridgelabz.employeepayrolljdbc;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestEmployeePayrollJsonRestAssure {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public EmployeePayroll[] getEmployeeList() {
		Response response = RestAssured.get("/employees");
		EmployeePayrollService.LOG.info("Employee payroll entries in JSON Server :\n" + response.asString());
		EmployeePayroll[] arrayOfEmployees = new Gson().fromJson(response.asString(), EmployeePayroll[].class);
		return arrayOfEmployees;
	}

	public Response addEmployeeToJsonServer(EmployeePayroll employeePayroll) {
		String empJson =  new Gson().toJson(employeePayroll);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(empJson);
		return request.post("/employees");
	}

	@Test
	public void givenNewEmployeeWhenAddedShouldMatch() {
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

}
