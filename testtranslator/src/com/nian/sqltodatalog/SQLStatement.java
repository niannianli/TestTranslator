package com.nian.sqltodatalog;

public class SQLStatement {
	// parse input, and decide which SQL statement we get
	// for now: we have CREATE, INSERT, VIEW (SELECT ...)...
	protected String input;

	public SQLStatement(String input) {
		this.input = input;
	}

	public StatementType getStatementType() {

		if (isCreate())
			return StatementType.CREATE;
		if (isInsert())
			return StatementType.INSERT;
		if (isView())
			return StatementType.VIEW;

		return StatementType.UNKNOW;

	}

	private boolean isCreate() {

		String[] arr = input.split(" ");

		if (arr[0].toUpperCase().equals("CREATE") && arr[1].toUpperCase().equals("OR")
				&& arr[2].toUpperCase().equals("REPLACE") && arr[3].toUpperCase().equals("TABLE")) {
			return true;
		}
		return false;
	}

	private boolean isInsert() {
		String[] arr = input.split(" ");

		if (arr[0].toUpperCase().equals("INSERT") && arr[1].toUpperCase().equals("INTO")) {
			return true;
		}
		return false;
	}

	private boolean isView() {
		String[] arr = input.split(" ");

		if (arr[0].toUpperCase().equals("CREATE") && arr[1].toUpperCase().equals("OR")
				&& arr[2].toUpperCase().equals("REPLACE") && arr[3].toUpperCase().equals("VIEW")) {
			return true;
		}
		return false;
	}

	public String getInput() {
		return input;
	}
}
