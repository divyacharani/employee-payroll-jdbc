package com.bridgelabz.employeepayrolljdbc;

public class DatabaseException extends Exception {

	public enum exceptionType {

		DRIVER_CONNECTION, DATABASE_CONNECTION, EXECUTE_QUERY
	}

	exceptionType type;

	public DatabaseException(String message, exceptionType type) {
		super(message);
		this.type = type;
	}
}
