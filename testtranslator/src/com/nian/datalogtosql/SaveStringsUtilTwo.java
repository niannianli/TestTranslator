package com.nian.datalogtosql;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveStringsUtilTwo {

	// talbe/view names
	private static ArrayList<String> factsPredicates;

	// String is table name; ArrayList is variables; each table has one list of
	// variables
	private static HashMap<String, ArrayList<String>> factsWithVariables;

	// String is table name; ArrayList is values; each table can have many records,
	// a list of records, each record is an array of values
	private static HashMap<String, ArrayList<String[]>> factsWithValues;

	private static ArrayList<String> rulesPredicates;

	private static HashMap<String, ArrayList<String>> rulesWithVariables;

	// String is header, aka, view name; ArrayList is rules; for now, each rule
	// matches one view, one view matches a list of variables
	private static ArrayList<String> rulesToSQL;

	// String is header, aka, view name, newly created sub-table for selected
	// records;
	// ArrayList is rules: negation or recursive
	// in our case: negation only: aka, stritification, negate IDB
	// in this case: we have one example
	// this computed rule is negation
	// original rule must exist previously
	// not used for now
	private static ArrayList<String> computedRulesToSQL;

	public static ArrayList<String> getFactsPredicates() {
		return factsPredicates;
	}

	public static void setFactsPredicates(ArrayList<String> factsPredicates) {
		SaveStringsUtilTwo.factsPredicates = factsPredicates;
	}

	public static HashMap<String, ArrayList<String>> getFactsWithVariables() {
		return factsWithVariables;
	}

	public static void setFactsWithVariables(HashMap<String, ArrayList<String>> factsWithVariables) {
		SaveStringsUtilTwo.factsWithVariables = factsWithVariables;
	}

	public static HashMap<String, ArrayList<String[]>> getFactsWithValues() {
		return factsWithValues;
	}

	public static void setFactsWithValues(HashMap<String, ArrayList<String[]>> factsWithValues) {
		SaveStringsUtilTwo.factsWithValues = factsWithValues;
	}

	public static ArrayList<String> getRulesPredicates() {
		return rulesPredicates;
	}

	public static void setRulesPredicates(ArrayList<String> rulesPredicates) {
		SaveStringsUtilTwo.rulesPredicates = rulesPredicates;
	}

	public static HashMap<String, ArrayList<String>> getRulesWithVariables() {
		return rulesWithVariables;
	}

	public static void setRulesWithVariables(HashMap<String, ArrayList<String>> rulesWithVariables) {
		SaveStringsUtilTwo.rulesWithVariables = rulesWithVariables;
	}

	public static ArrayList<String> getRulesToSQL() {
		return rulesToSQL;
	}

	public static void setRulesToSQL(ArrayList<String> rulesToSQL) {
		SaveStringsUtilTwo.rulesToSQL = rulesToSQL;
	}

	public static ArrayList<String> getComputedRulesToSQL() {
		return computedRulesToSQL;
	}

	public static void setComputedRulesToSQL(ArrayList<String> computedRulesToSQL) {
		SaveStringsUtilTwo.computedRulesToSQL = computedRulesToSQL;
	}

}