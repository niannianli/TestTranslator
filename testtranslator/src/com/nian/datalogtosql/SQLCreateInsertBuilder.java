package com.nian.datalogtosql;

import java.util.ArrayList;

import org.springframework.util.StringUtils;

//these 2 are together:
//as table name, variable name, and variable type should be parsed from both
//values can only be inserted after tables are created

//get info after EvaluatorParser, for now: no evaluator, only parser
//then build SQL statements

//unlike SQL to Datalog: which parse and genereate Datalog at the same time
//as we have no evaluation for SQL, assume all syntax correct

//so for this case: 
//evaluate(to be done later), parse at the same time, save info

//later build SQL from saved info

public class SQLCreateInsertBuilder {

	protected String tableName;

	public SQLCreateInsertBuilder(String tableName) {
		this.tableName = tableName;
	}

	public String build() {

		StringBuffer sb = new StringBuffer();

		// for ...variables
		ArrayList<String> variables = SaveStringsUtilTwo.getFactsWithVariables().get(tableName);
		ArrayList<String> variablesWithTypes = SaveStringsUtilTwo.getFactsWithVariables().get(tableName);
		
		for(String s: variables) {
			String variableWithType = s + " " + "string";
			variablesWithTypes.add(variableWithType);
		}

		sb.append("CREATE OR REPLACE TABLE " + tableName + " ( "
				+ StringUtils.arrayToCommaDelimitedString(variablesWithTypes.toArray()) + " );" + "\n");

		// for...values
		ArrayList<String[]> values = SaveStringsUtilTwo.getFactsWithValues().get(tableName);

		for (String[] s : values) {

			sb.append("INSERT INTO " + tableName + " VALUES (" + StringUtils.arrayToCommaDelimitedString(s) + ");" + "\n");

		}

		return sb.toString();
	}

}