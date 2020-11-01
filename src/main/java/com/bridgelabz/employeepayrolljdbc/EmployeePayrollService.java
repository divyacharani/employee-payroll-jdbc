package com.bridgelabz.employeepayrolljdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bridgelabz.employeepayrolljdbc.EmployeePayrollDBService.statementType;

public class EmployeePayrollService {

	public static final Logger LOG = LogManager.getLogger(EmployeePayrollService.class);
	private static EmployeePayrollDBService employeePayrollDBService;
	List<EmployeePayroll> employeePayrollList = new ArrayList<>();

	public EmployeePayrollService() {
		employeePayrollDBService = employeePayrollDBService.getInstance();
	}

	public static void main(String[] args) {
		// Welcome Message
		LOG.info("Welcome to Employee Payroll Service");
	}

	// To read payroll Data from database
	public List<EmployeePayroll> readData() throws DatabaseException {
		employeePayrollList = employeePayrollDBService.readDataDB();
		return employeePayrollList;
	}

	// To update data in the database
	public void updateData(String name, double salary, statementType type) throws DatabaseException {
		employeePayrollList = employeePayrollDBService.readDataDB();
		int rowAffected = employeePayrollDBService.updateDataDB(name, salary, type);
		if (rowAffected != 0)
			(getEmployeeByName(employeePayrollList, name)).setSalary(salary);
	}

	private EmployeePayroll getEmployeeByName(List<EmployeePayroll> employeePayrollList, String name) {
		EmployeePayroll employee = employeePayrollList.stream()
				.filter(employeeObj -> ((employeeObj.getName()).equals(name))).findFirst().orElse(null);
		return employee;
	}

	// To check the database after updating
	public boolean checkEmployeeDataInSyncWithDatabase(String name) throws DatabaseException {
		boolean result = false;
		employeePayrollList = employeePayrollDBService.readDataDB();
		EmployeePayroll employee = employeePayrollDBService.getEmployeeDataByNameDB(name).get(0);
		result = (getEmployeeByName(employeePayrollList, name)).equals(employee);
		return result;
	}

	// To get employee data joined after a particular date
	public List<EmployeePayroll> getEmployeeDataByDate(LocalDate startDate, LocalDate endDate)
			throws DatabaseException {
		return employeePayrollDBService.getEmployeeDataByDateDB(startDate, endDate);
	}

	// To get sum of salaries of male and female employees
	public Map<String, Double> getSalarySumByGender() throws DatabaseException {
		return employeePayrollDBService.getSalarySumByGenderDB();
	}

	// To add new employee to database
	public void addEmployeeData(String name, char gender, double salary, LocalDate startDate) throws DatabaseException {
		EmployeePayroll employee = employeePayrollDBService.addEmployeeDataDB(name, gender, salary, startDate);
		if (employee.getId() != -1)
			employeePayrollList.add(employee);
	}

	// To add employee details to both tables
	public void addEmployeeToEmployeeAndPayroll(String name, char gender, double salary, LocalDate startDate)
			throws DatabaseException {
		EmployeePayroll employee = employeePayrollDBService.addEmployeeToEmployeeAndPayrollDB(name, gender, salary, startDate);
		if (employee.getId() != -1)
			employeePayrollList.add(employee);
	}
}
