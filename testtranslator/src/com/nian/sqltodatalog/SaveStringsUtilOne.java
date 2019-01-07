package com.nian.sqltodatalog;

import java.util.ArrayList;
import java.util.Map;

public class SaveStringsUtilOne {

	// while parsing SQL statements, save below items:

	// we save a list of tableNames to add to output Datalog program
	private static ArrayList<String> tables;

	// we save the tableName and its variables to add to output Datalog program
	// each table can only have one list of variables
	private static Map<String, ArrayList<String>> tableNameVariables;

	// we save the taleName and its values to add to output Datalog program
	// each table can have many records/arrays, saved as a list of arrays/records
	private static Map<String, ArrayList<String[]>> tableNameValues;

	private static Map<String, ArrayList<String>> viewNameVariables;

	private static Map<String, ArrayList<String>> selectNameVariables;

	public static ArrayList<String> getTables() {
		return tables;
	}

	public static void setTables(ArrayList<String> tables) {
		SaveStringsUtilOne.tables = tables;
	}

	public static Map<String, ArrayList<String>> getTableNameVariables() {
		return tableNameVariables;
	}

	public static void setTableNameVariables(Map<String, ArrayList<String>> tableNameVariables) {
		SaveStringsUtilOne.tableNameVariables = tableNameVariables;
	}

	public static Map<String, ArrayList<String[]>> getTableNameValues() {
		return tableNameValues;
	}

	public static void setTableNameValues(Map<String, ArrayList<String[]>> tableNameValues) {
		SaveStringsUtilOne.tableNameValues = tableNameValues;
	}

	public static Map<String, ArrayList<String>> getViewNameVariables() {
		return viewNameVariables;
	}

	public static void setViewNameVariables(Map<String, ArrayList<String>> viewNameVariables) {
		SaveStringsUtilOne.viewNameVariables = viewNameVariables;
	}

	public static Map<String, ArrayList<String>> getSelectNameVariables() {
		return selectNameVariables;
	}

	public static void setSelectNameVariables(Map<String, ArrayList<String>> selectNameVariables) {
		SaveStringsUtilOne.selectNameVariables = selectNameVariables;
	}

}
