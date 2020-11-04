package com.bridgelabz.employeepayrolljdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bridgelabz.employeepayrolljdbc.EmployeePayrollDBService.statementType;

public class EmployeePayrollService {

	public static Logger LOG = LogManager.getLogger(EmployeePayrollService.class);
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
		EmployeePayroll employee = employeePayrollDBService.addEmployeeToEmployeeAndPayrollDB(name, gender, salary,
				startDate);
		if (employee.getId() != -1)
			employeePayrollList.add(employee);
	}

	public void addEmployeeToAllRelatedTables(EmployeePayroll employeePayroll) throws DatabaseException {
		EmployeePayroll employee = employeePayrollDBService.addEmployeeToAllRelatedTablesDB(employeePayroll);
		if (employee.getId() != -1)
			employeePayrollList.add(employee);
	}

	public void removeEmployeeFromPayrollTable(String name) throws DatabaseException {
		employeePayrollList = employeePayrollDBService.readDataDB();
		int rowAffected = employeePayrollDBService.removeEmployeeFromPayrollTableDB(name);
		if (rowAffected != 0) {
			EmployeePayroll employee = getEmployeeByName(employeePayrollList, name);
			employeePayrollList.remove(employee);
		}
	}

	public boolean checkActiveStatus(String name) throws DatabaseException {
		return employeePayrollDBService.checkActiveStatusDB(name);
	}

	public void addEmployeeListToEmployeeAndPayrollTable(List<EmployeePayroll> employeeList) throws DatabaseException {
		for (EmployeePayroll employee : employeeList) {
		addEmployeeToEmployeeAndPayroll(employee.getName(), employee.getGender(), employee.getSalary(), employee.getStartDate());
		}
		
	}
	
	public void addEmployeeListToEmployeeAndPayrollWithThreads(List<EmployeePayroll> employeeList) {
		Map<Integer, Boolean> employeeAditionStatus = new HashMap<>();
		employeeList.forEach(employee -> {
			Runnable task = () -> {
				employeeAditionStatus.put(employee.hashCode(), false);
				LOG.info("Employee being added : " + employee.getName());
				try {
					addEmployeeToEmployeeAndPayroll(employee.getName(), employee.getGender(), employee.getSalary(),
							employee.getStartDate());
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				employeeAditionStatus.put(employee.hashCode(), true);
				LOG.info("Employee added : " + employee.getName());
			};
			Thread thread = new Thread(task, employee.getName());
			thread.start();
		});

		while (employeeAditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
	}

	public void updateSalaryList(Map<String, Double> nameToUpdatedSalary) {
		Map<Integer, Boolean> salaryUpdateStatus = new HashMap<>();
		nameToUpdatedSalary.forEach((employeeName, salary) -> {
			Runnable task = () -> {
				salaryUpdateStatus.put(employeeName.hashCode(), false);
				try {
					updateData(employeeName, salary, statementType.STATEMENT);
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
				salaryUpdateStatus.put(employeeName.hashCode(), true);
			};
			Thread thread = new Thread(task, employeeName);
			thread.start();
		});

		while (salaryUpdateStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
