package com.bridgelabz.employeepayrolljdbc;

import java.time.LocalDate;

public class EmployeePayroll {

	private int id;
	private String name;
	private char gender;
	private Double salary;
	private LocalDate startDate;

	// Constructor

	public EmployeePayroll(int id, String name, char gender, Double salary, LocalDate startDate) {
		this(name, gender, salary, startDate);
		this.id = id;
	}

	public EmployeePayroll(String name, char gender, Double salary, LocalDate startDate) {
		this.name = name;
		this.gender = gender;
		this.salary = salary;
		this.startDate = startDate;
	}
	
	public EmployeePayroll(int id, String name, Double salary, LocalDate startDate) {
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.startDate = startDate;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "EmployeePayroll [id=" + id + ", name=" + name + ", salary=" + salary + ", startDate=" + startDate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayroll other = (EmployeePayroll) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (salary == null) {
			if (other.salary != null)
				return false;
		} else if (!salary.equals(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
