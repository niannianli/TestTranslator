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

	boolean isUnion = false;
	boolean isDifference = false;

	boolean isProjection = false;
	boolean isSelection = false;
	
	boolean parsingEnded = false;

	// Infers and computes all the facts in a positive datalog program
	// or IDB negation as only one example: stratification
	public void parse(String input) {

		factsPredicates = SaveStringsUtilTwo.getFactsPredicates();
		if (factsPredicates == null) {
			factsPredicates = new ArrayList<String>();
		}

		factsWithVariables = SaveStringsUtilTwo.getFactsWithVariables();
		if (factsWithVariables == null) {
			factsWithVariables = new HashMap<String, ArrayList<String>>();
		}

		factsWithValues = SaveStringsUtilTwo.getFactsWithValues();
		if (factsWithValues == null) {
			factsWithValues = new HashMap<String, ArrayList<String[]>>();
		}

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}

		rulesWithVariables = SaveStringsUtilTwo.getRulesWithVariables();
		if (rulesWithVariables == null) {
			rulesWithVariables = new HashMap<String, ArrayList<String>>();
		}

		rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();
		if (rulesToSQL == null) {
			rulesToSQL = new ArrayList<String>();
		}

		computedRulesToSQL = SaveStringsUtilTwo.getComputedRulesToSQL();
		if (computedRulesToSQL == null) {
			computedRulesToSQL = new ArrayList<String>();
		}

		// no . anymore
		String[] strings = input.replaceAll("\n", "").split("\\.");

		for (String s : strings) {
			if (s.contains(":-")) {

				if (s.contains("=") || s.contains(">") || s.contains("<")) {
					isSelection = true;
					parseSelectionRule(s);
				} else if (s.contains("fj") || s.contains("rj") || s.contains("lj") || s.contains("ij")) {
					
					if (s.contains("fj")) {
						isFullJoin = true;
					}

					if (s.contains("rj")) {
						isRightJoin = true;
					}

					if (s.contains("lj")) {
						isLeftJoin = true;
					}

					if (s.contains("ij")) {
						isInnerJoin = true;
					}

					parseJoinRule(s);
				} else if (s.contains(";")) {
					isUnion = true;
					parseUnionRule(s);
				} else if (s.contains("¬")) {
					isDifference = true;
					parseDifferenceRule(s);
				} else {
					isProjection = true;
					parseProjectionRule(s);
				}

			} else {
				parseEDBFactsValues(s);
			}

		}

		SaveStringsUtilTwo.setFactsPredicates(factsPredicates);
		SaveStringsUtilTwo.setFactsWithVariables(factsWithVariables);
		SaveStringsUtilTwo.setFactsWithValues(factsWithValues);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);
		SaveStringsUtilTwo.setRulesToSQL(rulesToSQL);
		SaveStringsUtilTwo.setComputedRulesToSQL(computedRulesToSQL);
	}

	public void parseProjectionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables.put(rulePredicateName, variabless);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parsingEnded = true;
		parseFactVariables(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	public void parseSelectionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables.put(rulePredicateName, variabless);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parseCondition(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	public void parseJoinRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables.put(rulePredicateName, variabless);

		String newBodyString = bodyString.substring(3, bodyString.length());
		parseJoin(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	public void parseUnionRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables.put(rulePredicateName, variabless);

		String newBodyString = bodyString.substring(1, bodyString.length());
		parseUnion(rulePredicateName, newBodyString, sbHeader, sbBody);

	}

	public void parseDifferenceRule(String rule) {

		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		String headerString = s1[0];
		String bodyString = s1[1];

		String[] s = headerString.replaceAll(" ", "").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");

		// a,b)
		// one space A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();

		for (int i = 0; i < variables.length; i++) {
			variabless.add(variables[i]);
		}

		rulesWithVariables.put(rulePredicateName, variabless);
		
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

			for (String ruleVariable : rulesWithVariables.get(rulePredicateName)) {
				if (ruleVariable.equals(s4)) {
					countOfVariables++;
				}
			}
		}

		factsWithVariables.put(factPredicateName, variables);

		if (isProjection) {

			if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
				sbHeader.append("* ");
			} else {
				sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}

			sbBody.append(" FROM " + factPredicateName);

		} else if (isSelection) {

			if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
				sbHeader.append("* ");
			} else {
				sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}

			sbBody.append(" FROM " + factPredicateName);

		} else if (isFullJoin || isRightJoin || isLeftJoin || isInnerJoin) {
			if (!sbHeader.toString().contains("*")) {
				sbHeader.append("," + StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}
			if (sbBody.toString().contains(" FROM ")) {
				sbBody.append(factPredicateName);
			} else {
				sbBody.append(" FROM " + factPredicateName);
			}

		} else if (isUnion || isDifference) {
			if (factsWithVariables.get(factPredicateName).size() == countOfVariables) {
				sbHeader.append("* ");
			} else {
				sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
			}

			sbBody.append(" FROM " + factPredicateName);
		}
		
		if(parsingEnded) {
		sbHeader.append(sbBody.toString());
		rulesToSQL.add(sbHeader.toString() + ";" + "\n");
		
		isProjection = false;
			isUnion = false;
		isDifference = false;
		
		parsingEnded = false;
		
		}
		
		}

	// one e.g.: A(a), a='a2'
	public void parseCondition(String rulePredicateName, String bodyString, StringBuilder sbHeader,
			StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");

		for (int i = 0; i < subGoals.length; i++) {
//a='a2'
			if (subGoals[i].contains("=") || subGoals[i].contains("<") || subGoals[i].contains(">")) {
				sbBody.append(" WHERE " + subGoals[i]);
				
				sbHeader.append(sbBody.toString());
				rulesToSQL.add(sbHeader.toString() + ";" + "\n");
				isSelection = false;
				
			} else {
				// A(a),
				// A(a)
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
			}

		}

	}

	// one e.g.: ij(A(a), B(a), a=b)
	public void parseJoin(String rulePredicateName, String bodyString, StringBuilder sbHeader, StringBuilder sbBody) {

		String mainString = null;

		// one e.g.: ij(A(a), B(a), a=b)
		mainString = bodyString.substring(2, bodyString.length());

		// (A(a), B(a), a=b)
		String[] s = mainString.split(" ");

		for (int i = 0; i < s.length; i++) {
			// (A(a),
			if (s[i].charAt(0) == '(') {
				// A(a)
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
				for (String newVariable : newVariables) {

					for (String factPredicate : factsPredicates) {

						if (factsWithVariables.get(factPredicate) != null) {

							ArrayList<String> realVariables = factsWithVariables.get(factPredicate);

							for (int j = 0; j < realVariables.size(); j++)

								if (realVariables.get(j).equals(newVariable)) {

									if (j == realVariables.size() - 1) {
										sbBody.append(factPredicate + "." + newVariable);
									} else {
										sbBody.append(factPredicate + "." + newVariable + "=");
									}
								}

						}
					}

				}
				
				sbHeader.append(sbBody.toString());
				rulesToSQL.add(sbHeader.toString() + ";" + "\n");
				isFullJoin = false;
				isRightJoin = false;
				isLeftJoin = false;
				isInnerJoin = false;

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
				parseFactVariables(rulePredicateName, s[i].substring(0, s[i].length() - 1), sbHeader, sbBody);

			}

		}
	}

	// one e.g.: A(a); B(b)
	// A(a), ¬B(b)
	// for now, we only test 2 tables join
	public void parseUnion(String rulePredicateName, String bodyString, StringBuilder sbHeader, StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");
		// A(a);
		// B(b)
		for (int i = 0; i < subGoals.length; i++) {

			if (subGoals[i].contains(";")) {
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
				sbBody.append(" UNION SELECT ");
			} else {
				parsingEnded = true;
				parseFactVariables(rulePredicateName, subGoals[i], sbHeader, sbBody);
			}
		}
	}

	public void parseDifference(String rulePredicateName, String bodyString, StringBuilder sbHeader,
			StringBuilder sbBody) {

		String[] subGoals = bodyString.split(" ");
		// A(a);
		// B(b)
		for (int i = 0; i < subGoals.length; i++) {

			if (!subGoals[i].contains("¬")) {
				parseFactVariables(rulePredicateName, subGoals[i].substring(0, subGoals[i].length() - 1), sbHeader,
						sbBody);
				sbBody.append(" EXCEPT SELECT ");
			} else {
				parsingEnded = true;
				parseFactVariables(rulePredicateName, subGoals[i].substring(1, subGoals[i].length()), sbHeader, sbBody);
			}
		}

	}

	// one e.g.: C('a1','a1')
	public void parseEDBFactsValues(String edbValues) {

		String[] s1 = edbValues.split("\\(");

		// facts must exist already, more code to be added ...

		// C
		// 'a1','a1')
		String factPredicateName = s1[0].replaceAll(" ", "");

		boolean factPredicateNameExists = false;

		for (String ss : factsPredicates) {
			if (ss.equals(factPredicateName)) {
				factPredicateNameExists = true;
			}
		}

		if (!factPredicateNameExists) {
			factsPredicates.add(factPredicateName);
		}

		// 'a1','a1')
		String s2 = s1[1];

		// 'a1','a1'
		String[] s3 = s2.replaceAll("\\)", "").split(",");

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

	}

}