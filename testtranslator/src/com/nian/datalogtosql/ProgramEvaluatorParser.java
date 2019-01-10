package com.nian.datalogtosql;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.util.StringUtils;

//to be continued...more work to be added...

//evaluate and parse at the same time, save all info
//move to different SQL builder to generate/output SQL statements

//no evaluate for now
//only parse
public class ProgramEvaluatorParser {

	// input: another format
	// i am not using the framwork for now

	// parse directly

	// if this is better, will use it later
	// Mapping mapping;

	// mapping is not used for now: another format of input
	public ProgramEvaluatorParser(DatalogProgram datalogProgram) {
		// this.mapping = datalogProgram.getMapping();
	}

	// save all info: to be used to build SQL statements later
	ArrayList<String> factsPredicates;
	HashMap<String, ArrayList<String>> factsWithVariables;
	HashMap<String, ArrayList<String[]>> factsWithValues;
	ArrayList<String> rulesPredicates;
	HashMap<String, ArrayList<String>> rulesWithVariables;
	ArrayList<String> rulesToSQL;
	ArrayList<String> computedRulesToSQL;

	boolean isFullJoin = false;
	boolean isRightJoin = false;
	boolean isLeftJoin = false;
	boolean isInnerJoin = false;
	int numOfTables = 0;

	boolean isUnion = false;
	boolean isDifference = false;

	boolean isProjection = false;
	boolean isSelection = false;

	boolean parsingEnded = false;

	// Infers and computes all the facts in a positive datalog program
	// or IDB negation as only one example: stratification
	public void parse(String input) {

		// no . anymore
		String[] strings = input.replaceAll("\n", "").split("\\.");

		for (String s : strings) {
			if (s.contains(":-")) {

				// INNER_JOIN_RULE(a) :- ij(A(a), B(b), a=b)

				String[] newString = s.split(":-");
				// ij(A(a), B(b), a=b)
				// index 0, 1, 2
				String newBodyString = newString[1];

				if (

				(newBodyString.charAt(1) == 'i' && newBodyString.charAt(2) == 'j')
						|| (newBodyString.charAt(1) == 'f' && newBodyString.charAt(2) == 'j')
						|| (newBodyString.charAt(1) == 'l' && newBodyString.charAt(2) == 'j')
						|| (newBodyString.charAt(1) == 'r' && newBodyString.charAt(2) == 'j')

				) {

					if (newBodyString.charAt(1) == 'f' && newBodyString.charAt(2) == 'j') {
						isFullJoin = true;
					}

					if (newBodyString.charAt(1) == 'r' && newBodyString.charAt(2) == 'j') {
						isRightJoin = true;
					}

					if (newBodyString.charAt(1) == 'l' && newBodyString.charAt(2) == 'j') {
						isLeftJoin = true;
					}

					if (newBodyString.charAt(1) == 'i' && newBodyString.charAt(2) == 'j') {
						isInnerJoin = true;
					}

					parseJoinRule(s);
					continue;

				}

				else if (s.contains("=") || s.contains(">") || s.contains("<")) {
					isSelection = true;
					parseSelectionRule(s);
					continue;
				}

				else if (s.contains(";")) {
					isUnion = true;
					parseUnionRule(s);
					continue;
				} else if (s.contains("¬")) {
					isDifference = true;
					parseDifferenceRule(s);
					continue;
				} else {
					isProjection = true;
					parseProjectionRule(s);
					continue;
				}

			} else {
				parseEDBFactsValues(s);
			}

		}
	}

	// PROJECTION_RULE(a) :- A(a)
	public void parseProjectionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s2 = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s2[0];

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}
		rulesPredicates.add(rulePredicateName);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s2[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}
		rulesWithVariables.put(rulePredicateName, variabless);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);

		// A(a)
		String newBodyString = bodyString.substring(1, bodyString.length());
		parsingEnded = true;
		parseFactVariables(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	// SELECTION_RULE(a) :- A(a), a='a2'
	public void parseSelectionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s2 = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s2[0];

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}
		rulesPredicates.add(rulePredicateName);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s2[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}
		rulesWithVariables.put(rulePredicateName, variabless);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parseCondition(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	// INNER_JOIN_RULE(a) :- ij(A(a), B(b), a=b)
	public void parseJoinRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s2 = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s2[0];

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}
		rulesPredicates.add(rulePredicateName);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		// ij(A(a), B(b), a=b)
		String[] variables = ((s2[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}
		rulesWithVariables.put(rulePredicateName, variabless);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);

		String newBodyString = bodyString.substring(3, bodyString.length());
		parseJoin(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	// UNION_RULE(a,b) :- A(a); B(b)
	public void parseUnionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s2 = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s2[0];

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}
		rulesPredicates.add(rulePredicateName);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s2[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}
		rulesWithVariables.put(rulePredicateName, variabless);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parseUnion(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	// DIFFERENCE_RULE(a) :- A(a), ¬B(b)
	public void parseDifferenceRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s2 = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s2[0];

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}
		rulesPredicates.add(rulePredicateName);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s2[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}
		rulesWithVariables.put(rulePredicateName, variabless);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parseDifference(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	// one e.g.: A(a)
	public void parseFactVariables(String rulePredicateName, String bodyString, StringBuilder sbHeader,
			StringBuilder sbBody) {

		// projection rule, one predicate only
		// A(a)
		String[] s1 = bodyString.split("\\(");

		String factPredicateName = s1[0].replaceAll(" ", "");

		ArrayList<String> variables = new ArrayList<String>();

		int countOfVariables = 0;

		// predicateName should always exist: safe Datalog rule
		// a)
		String s2 = s1[1];
		// a
		String news2 = s2.substring(0, s2.length() - 1);
		String[] s3 = news2.split(",");

		for (String s4 : s3) {
			variables.add(s4);

			rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
			if (rulesWithVariables == null) {
				rulesWithVariables = new HashMap<String, ArrayList<String>>();
			}
			for (String ruleVariable : rulesWithVariables.get(rulePredicateName)) {
				if (ruleVariable.equals(s4)) {
					countOfVariables++;
				}
			}
		}

		factsWithVariables = SaveStringsUtilTwo.getFactsWithVariables();
		if (factsWithVariables == null) {
			factsWithVariables = new HashMap<String, ArrayList<String>>();
		}
		factsWithVariables.put(factPredicateName, variables);
		SaveStringsUtilTwo.setFactsWithVariables(factsWithVariables);

		// only one table
		if (isProjection || isSelection) {

			if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
				sbHeader.append("* ");
			} else {
				sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}

			sbBody.append(" FROM " + factPredicateName);

			// 2 tables
		}

		else if (isFullJoin || isRightJoin || isLeftJoin || isInnerJoin) {

			if (numOfTables == 1) {

				if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
					sbHeader.append("* ");
				} else {
					sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
				}

			} else if (!sbHeader.toString().contains("*") && numOfTables > 1) {
				sbHeader.append("," + StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}

			if (sbBody.toString().contains(" FROM ")) {
				sbBody.append(factPredicateName);
			} else {
				sbBody.append(" FROM " + factPredicateName);
			}

		} else if (isUnion || isDifference) {
			if (numOfTables == 1) {
				if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
					sbHeader.append("* ");
				} else {
					sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
				}

				sbBody.append(" FROM " + factPredicateName);
			} else if (numOfTables > 1) {
				if ((factsWithVariables.get(factPredicateName).size() == countOfVariables) || countOfVariables == 0) {
					sbBody.append("* ");
				} else {
					sbBody.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
				}

				sbBody.append(" FROM " + factPredicateName);
			}
		}

		if (parsingEnded) {
			sbHeader.append(sbBody.toString());
			rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();
			if (rulesToSQL == null) {
				rulesToSQL = new ArrayList<String>();
			}
			rulesToSQL.add(sbHeader.toString() + ";" + "\n");
			SaveStringsUtilTwo.setRulesToSQL(rulesToSQL);

			isProjection = false;
			isUnion = false;
			isDifference = false;

			parsingEnded = false;
			numOfTables = 0;

		}

	}

	// one e.g.: A(a), a='a2'
	// A(a), a='a2'
	public void parseCondition(String rulePredicateName, String bodyString, StringBuilder sbHeader,
			StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");

		for (int i = 0; i < subGoals.length; i++) {
//a='a2'
			if (subGoals[i].contains("=") || subGoals[i].contains("<") || subGoals[i].contains(">")) {
				sbBody.append(" WHERE " + subGoals[i]);

				sbHeader.append(sbBody.toString());
				rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();
				if (rulesToSQL == null) {
					rulesToSQL = new ArrayList<String>();
				}
				rulesToSQL.add(sbHeader.toString() + ";" + "\n");
				SaveStringsUtilTwo.setRulesToSQL(rulesToSQL);
				isSelection = false;
				numOfTables = 0;

			} else {
				// A(a),
				// A(a)
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
			}

		}

	}

	// (A(a), B(b), a=b)
	public void parseJoin(String rulePredicateName, String bodyString, StringBuilder sbHeader, StringBuilder sbBody) {

		// (A(a), B(a), a=b)
		String[] s = bodyString.split(" ");

		for (int i = 0; i < s.length; i++) {
			// (A(a),
			if (s[i].charAt(0) == '(') {
				// A(a)
				numOfTables = 1;
				parseFactVariables(rulePredicateName, s[i].substring(1, s[i].length() - 1), sbHeader, sbBody);
			} else if (s[i].contains("=")) {
				sbBody.append(" ON ");

				// variables check
				// a=b)
				// A.a=B.b;
				// a=b)
				int lastIndex = s[i].length() - 1;

				String newSubGoal = null;

				// a=b
				if (s[i].charAt(lastIndex) == ')') {
					newSubGoal = s[i].substring(0, lastIndex);
				}

				String[] newVariables = newSubGoal.split("\\=");

				// a b
				// for (String newVariable : newVariables) {

				for (int k = 0; k < newVariables.length; k++) {
					factsPredicates = SaveStringsUtilTwo.getFactsPredicates();
					for (String factPredicate : factsPredicates) {

						factsWithVariables = SaveStringsUtilTwo.getFactsWithVariables();
						if (factsWithVariables.get(factPredicate) != null) {

							ArrayList<String> realVariables = factsWithVariables.get(factPredicate);

							for (int j = 0; j < realVariables.size(); j++)

								if (realVariables.get(j).equals(newVariables[k])) {

									if (k == newVariables.length - 1) {
										sbBody.append(factPredicate + "." + newVariables[k]);
									} else {
										sbBody.append(factPredicate + "." + newVariables[k] + "=");
									}
								}

						}
					}

				}

				sbHeader.append(sbBody.toString());
				rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();
				if (rulesToSQL == null) {
					rulesToSQL = new ArrayList<String>();
				}
				rulesToSQL.add(sbHeader.toString() + ";" + "\n");
				SaveStringsUtilTwo.setRulesToSQL(rulesToSQL);
				isFullJoin = false;
				isRightJoin = false;
				isLeftJoin = false;
				isInnerJoin = false;
				numOfTables = 0;

			} else {

				if (isFullJoin) {
					sbBody.append(" FULL JOIN ");
				}

				if (isLeftJoin) {
					sbBody.append(" LEFT JOIN ");
				}

				if (isRightJoin) {
					sbBody.append(" RIGHT JOIN ");
				}

				if (isInnerJoin) {
					sbBody.append(" INNER JOIN ");
				}

				// B(b),
				numOfTables = 2;
				parseFactVariables(rulePredicateName, s[i].substring(0, s[i].length() - 1), sbHeader, sbBody);

			}

		}
	}

	// one e.g.: A(a); B(b)
	// A(a), ¬B(b)
	// for now, we only test 2 tables join
	// A(a); B(b)
	public void parseUnion(String rulePredicateName, String bodyString, StringBuilder sbHeader, StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");
		// A(a);
		// B(b)
		for (int i = 0; i < subGoals.length; i++) {

			if (subGoals[i].contains(";")) {
				numOfTables = 1;
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
				sbBody.append(" UNION SELECT ");
			} else {
				parsingEnded = true;
				numOfTables = 2;
				parseFactVariables(rulePredicateName, subGoals[i], sbHeader, sbBody);
			}
		}
	}

	// A(a), ¬B(b)
	public void parseDifference(String rulePredicateName, String bodyString, StringBuilder sbHeader,
			StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");
		// A(a);
		// B(b)
		for (int i = 0; i < subGoals.length; i++) {

			if (!subGoals[i].contains("¬")) {
				numOfTables = 1;
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
				sbBody.append(" EXCEPT SELECT ");
			} else {
				parsingEnded = true;
				numOfTables = 2;
				parseFactVariables(rulePredicateName, subGoals[i].substring(1, subGoals[i].length()), sbHeader, sbBody);
			}
		}

	}

	// C('a2','b2')
	// one e.g.: C('a1','a1')
	// C('a2','b2')
	public void parseEDBFactsValues(String edbValues) {

		String[] s1 = edbValues.split("\\(");

		// facts must exist already, more code to be added ...

		// C
		// 'a1','a1')
		String factPredicateName = s1[0].replaceAll(" ", "");

		boolean factPredicateNameExists = false;

		factsPredicates = SaveStringsUtilTwo.getFactsPredicates();
		if (factsPredicates == null) {
			factsPredicates = new ArrayList<String>();
		}
		for (String ss : factsPredicates) {
			if (ss.equals(factPredicateName)) {
				factPredicateNameExists = true;
			}
		}

		if (!factPredicateNameExists) {
			factsPredicates.add(factPredicateName);
		}

		SaveStringsUtilTwo.setFactsPredicates(factsPredicates);

		// 'a1','a1')
		String s2 = s1[1];

		// 'a1','a1'
		String[] s3 = s2.replaceAll("\\)", "").split(",");

		factsWithValues = SaveStringsUtilTwo.getFactsWithValues();
		if (factsWithValues == null) {
			factsWithValues = new HashMap<String, ArrayList<String[]>>();
		}

		// already have a record here
		if (factsWithValues.get(factPredicateName) != null) {
			ArrayList<String[]> records = factsWithValues.get(factPredicateName);
			// add a new record there
			records.add(s3);
			factsWithValues.put(factPredicateName, records);
		} else {
			ArrayList<String[]> records = new ArrayList<String[]>();
			// add first record there
			records.add(s3);
			factsWithValues.put(factPredicateName, records);
		}

		SaveStringsUtilTwo.setFactsWithValues(factsWithValues);

	}

}