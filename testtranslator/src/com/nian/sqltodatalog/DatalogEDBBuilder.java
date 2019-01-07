package com.nian.sqltodatalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatalogEDBBuilder {

	private String input;

	public DatalogEDBBuilder(String input) {
		this.input = input;
	}

	//only save table name and variables for later use, do not print anything for datalog
	public String build() {

		String[] arr = input.split(" ");
		String tableName = arr[4];
		
		ArrayList<String> tables = SaveStringsUtilOne.getTables();
		if(tables ==null) {
			tables = new ArrayList<String>();
		}
		
		tables.add(tableName);
		
		SaveStringsUtilOne.setTables(tables);

		ArrayList<String> variables = new ArrayList<String>();
		String variable = null;
		for (int i = 5; i < arr.length - 1; i++) {
			if (i == 5) {
				variable = arr[i].replaceAll("\\(", "");
			} else {
				variable = arr[i];
			}
			i = i + 1;
			variables.add(variable);
		}
		
		Map<String, ArrayList<String>> tableNameVariables = SaveStringsUtilOne.getTableNameVariables();
		
		if(tableNameVariables==null) {
			tableNameVariables = new HashMap<String, ArrayList<String>>();
		}
		
		tableNameVariables.put(tableName, variables);
		SaveStringsUtilOne.setTableNameVariables(tableNameVariables);
		
		return "";
	}
}
