package com.nian.sqltodatalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatalogEDBWithValuesBuilder {

	protected String input;

	public DatalogEDBWithValuesBuilder(String input) {
		this.input = input;
	}

	public String build() {

		String[] arr = input.split(" ");
		String tableName = arr[2];

		// insert, meaning table should already be there
		ArrayList<String> tables = SaveStringsUtilOne.getTables();

		ArrayList<String[]> records = null;

		StringBuilder sb = new StringBuilder();
		
	    Map<String, ArrayList<String[]>> tableNameValues = SaveStringsUtilOne.getTableNameValues();
		
		if(tableNameValues==null) {
			tableNameValues = new HashMap<String, ArrayList<String[]>>();
		}

		for (String s : tables) {

			if (s.equals(tableName)) {

				records = tableNameValues.get(tableName);
				
			if(records == null) {
				records = new ArrayList<String[]>();
			}

				sb.append(tableName + arr[4]);

				String[] values = (s.toString().replaceAll("\\;\\(\\)", "")).split(",");

				records.add(values);

			}
		}
		
		tableNameValues.put(tableName, records);
		SaveStringsUtilOne.setTableNameValues(tableNameValues);

		return (sb.toString()).replace(';', '.') + "\n";

	}

}
