package com.nian.datalogtosql;

import java.util.ArrayList;

import org.springframework.util.StringUtils;

public class SQLBuilder {

	public SQLBuilder() {
	}

	public String build() {

		StringBuffer sb = new StringBuffer();

		// for ...variables
		ArrayList<String> tables = SaveStringsUtilTwo.getFactsPredicates();

		for (String tableName : tables) {
			
			ArrayList<String> variables = SaveStringsUtilTwo.getFactsWithVariables().get(tableName);
			ArrayList<String> variablesWithTypes = new ArrayList<String>();

			for (String s : variables) {
				String variableWithType = s + " " + "string";
				variablesWithTypes.add(variableWithType);
			}

			sb.append("CREATE OR REPLACE TABLE " + tableName + " ("
					+ StringUtils.arrayToCommaDelimitedString(variablesWithTypes.toArray()) + ");" + "\n");

		}

		// for...values
		for (String tableName : tables) {

			ArrayList<String[]> values = SaveStringsUtilTwo.getFactsWithValues().get(tableName);

			for (String[] s : values) {

				sb.append("INSERT INTO " + tableName + " VALUES (" + StringUtils.arrayToCommaDelimitedString(s) + ");"
						+ "\n");

			}

		}

		ArrayList<String> rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();

		for (String s : rulesToSQL) {
			sb.append(s);
		}

		return sb.toString();
	}
}
