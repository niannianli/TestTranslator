package com.nian.sqltodatalog;

//from SQL to Datalog
public class SQLToDatalogTranslator {

	private String input;

	public SQLToDatalogTranslator(String input) {
		this.input = input;
	}

	public String toDatalog() {
		DatalogBuilder datalogBuilder = new DatalogBuilder();

		StringBuilder sb = new StringBuilder();

		// all SQL statements end with ;
		// all Datalog programs end with .
		String[] arr = input.replaceAll("\n", "").split(";");

		for (String s : arr) {

			SQLStatement sqlStatement = new SQLStatement(s);

			if (sqlStatement.getStatementType().equals(StatementType.CREATE)) {
				sb.append(datalogBuilder.createEDB(s).build());
			}

			if (sqlStatement.getStatementType().equals(StatementType.INSERT)) {
				sb.append(datalogBuilder.createEDBWithValues(s).build());
			}

			if (sqlStatement.getStatementType().equals(StatementType.VIEW)) {
				sb.append(datalogBuilder.createIDB(s).build());
			}

		}

		return sb.toString();
	}
}
