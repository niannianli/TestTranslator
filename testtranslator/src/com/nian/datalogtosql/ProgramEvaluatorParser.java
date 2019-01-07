package com.nian.datalogtosql;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.util.StringUtils;

import fr.univlyon1.mif37.dex.mapping.Mapping;

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
	//Mapping mapping;

	// mapping is not used for now: another format of input
	public ProgramEvaluatorParser(DatalogProgram datalogProgram) {
	//	this.mapping = datalogProgram.getMapping();
	}

	//save all info: to be used to build SQL statements later
	ArrayList<String> factsPredicates;
	ArrayList<String> rulesPredicates;
	HashMap<String, ArrayList<String>> factsWithVariables;
	HashMap<String, ArrayList<String[]>> factsWithValues;
	HashMap<String, ArrayList<String>> rulesWithVariables;
	ArrayList<String> rulesToSQL;
	ArrayList<String> computedRulesToSQL;
	
	StringBuilder sbHeader;
	StringBuilder sbBody;
	
	boolean isFullJoin = false;
	boolean isRightJoin = false;
	boolean isLeftJoin = false;
	boolean isInnerJoin = false;
	
	boolean isUnion = false;
	boolean isDifference = false;
	
	boolean isProjection = false;
	boolean isSelection = false;

	// Infers and computes all the facts in a positive datalog program
	// or IDB negation as only one example: stratification
	public void parse(String input) {

		factsPredicates = SaveStringsUtilTwo.getFactsPredicates();
		if (factsPredicates == null) {
			factsPredicates = new ArrayList<String>();
		}

		rulesPredicates = SaveStringsUtilTwo.getRulesPredicates();
		if (rulesPredicates == null) {
			rulesPredicates = new ArrayList<String>();
		}

		factsWithVariables = SaveStringsUtilTwo.getFactsWithVariables();
		if (factsWithVariables == null) {
			factsWithVariables = new HashMap<String, ArrayList<String>>();
		}

		factsWithValues = SaveStringsUtilTwo.getFactsWithValues();
		if (factsWithValues == null) {
			factsWithValues = new HashMap<String, ArrayList<String[]>>();
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

		//no . anymore
		String[] strings = input.replaceAll("\n", "").split("\\.");

		for (String s : strings) {
			if (s.contains(":-")) {
				parseIDBRule(s);
			} else {
				parseEDBFactsValues(s);
			}

		}

		SaveStringsUtilTwo.setFactsPredicates(factsPredicates);
		SaveStringsUtilTwo.setRulesPredicates(rulesPredicates);
		SaveStringsUtilTwo.setFactsWithVariables(factsWithVariables);
		SaveStringsUtilTwo.setFactsWithValues(factsWithValues);
		SaveStringsUtilTwo.setRulesWithVariables(rulesWithVariables);
		SaveStringsUtilTwo.setRulesToSQL(rulesToSQL);
		SaveStringsUtilTwo.setComputedRulesToSQL(computedRulesToSQL);
	}

	//will be called for each rule
	public void parseIDBRule(String rule) {

		//UNION_RULE(a,b) one space
		//one space  A(a); B(b)
		//UNION_RULE(a,b) :- A(a); B(b).
		sbHeader = new StringBuilder();
		sbBody = new StringBuilder();

		String[] s1 = rule.split(":-");

		// header: view + variables
		parseHeader(s1[0], s1[1]);
	}

	// one e.g.: UNION(a,b)
	//UNION_RULE(a,b) one space
			//one space  A(a); B(b)
	public void parseHeader(String headerString, String bodyString) {

		//UNION_RULE
		//a,b)
		//one space  A(a); B(b)
		String[] s = headerString.replaceAll(" ","").split("\\(");

		String rulePredicateName = s[0];

		rulesPredicates.add(rulePredicateName);

		// one e.g.: CREATE OR REPLACE VIEW UNION_RULE AS SELECT 
		// * FROM A UNION SELECT * FROM B;
		sbHeader.append("CREATE OR REPLACE VIEW " + rulePredicateName + " AS SELECT ");
		
		//a,b)
				//one space  A(a); B(b)
		String[] variables = ((s[1]).replaceAll("\\)", "")).split("\\,");

		ArrayList<String> variabless = new ArrayList<String>();
	
		for(int i = 0; i<variables.length; i++) {
			variabless.add(variables[i]);	
		}
		
		rulesWithVariables.put(rulePredicateName, variabless);
		
		// body: subgoal , subgoal, ...: table + variables, or other conditions: 
		// one special case: negate
		////one space  A(a); B(b)
		String newBodyString = bodyString.substring(1, bodyString.length());
				parseBody(rulePredicateName, newBodyString);
	}

	// one e.g.: A(a); B(b).
	//A(a); B(b)
	public void parseBody(String rulePredicateName, String bodyString) {

		if (bodyString.contains("¬") || bodyString.contains(";")) {
			parseUnionOrDifference(rulePredicateName, bodyString);
			return;
		}
		if (bodyString.contains("fj") || bodyString.contains("rj") || bodyString.contains("lj")
				|| bodyString.contains("ij")) {
			parseJoin(rulePredicateName, bodyString);
			return;
		}
		// maybe more operators...
		if (bodyString.contains("=") || bodyString.contains(">") || bodyString.contains("<")) {
			parseCondition(rulePredicateName, bodyString);
			isSelection = true;
			return;
		}

		// at the end
		// we only test FactVariables this time
		//one space  A(a); B(b)
		parseFactRuleVariables(rulePredicateName, bodyString);
		isProjection = true;

	}

	// one e.g.: A(a)
	public void parseFactRuleVariables(String rulePredicateName, String bodyString) {
		
		// projection rule, one predicate only
		//A(a)
		String[] s1 = bodyString.split("\\(");

		String factPredicateName = s1[0];
		
		ArrayList<String> variables = new ArrayList<String>();
		
		int countOfVariables = 0;

		for (String s : factsPredicates) {

			// predicateName should always exist: safe Datalog rule
			if (s.equals(factPredicateName)) {
				//a)
				String s2 = s1[1];
				//a
				String news2 = s2.substring(0,s2.length()-1);
				String[] s3 = news2.split(",");
				for (String s4 : s3) {
					variables.add(s4);
					
					for(String ruleVariable : rulesWithVariables.get(rulePredicateName))
					{
						if(ruleVariable.equals(s4)) {
							countOfVariables++;
						}
					}
				}
				
				if (factsWithVariables.get(factPredicateName) == null) {
					factsWithVariables.put(factPredicateName, variables);
				}
				
				if(isFullJoin || isRightJoin || isLeftJoin || isInnerJoin) {
					if(!sbHeader.toString().contains("*")) {
					sbHeader.append(","+ StringUtils.arrayToCommaDelimitedString(variables.toArray()));
					}
					
					sbBody.append(factPredicateName);
					
				}else {
					
					//meaning all variables are selected
				if(factsWithVariables.get(factPredicateName).size() == countOfVariables) {
					sbHeader.append("* ");
				}else {
					sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
				}
				
				sbBody.append(" FROM " + factPredicateName);
				
				}
            break;
			}

		}
		
		if(isProjection) {
		sbHeader.append(sbBody.toString());
		rulesToSQL.add(sbHeader.toString() + ";" + "\n");
		}

	}

	//one e.g.: A(a), a='a2'
	public void parseCondition(String rulePredicateName, String bodyString) {

		String[] subGoals = bodyString.split(" ");

		for (String subGoal : subGoals) {
//a='a2'
			if (subGoal.contains("=") || subGoal.contains("<") || subGoal.contains(">")) {
				sbBody.append(" WHERE " + subGoal);
			} else {
				//A(a),
				//A(a)
				parseFactRuleVariables(rulePredicateName, subGoal.substring(0,subGoal.length()-1));
			}

		}
		
		sbHeader.append(sbBody.toString());
		rulesToSQL.add(sbHeader.toString()+ ";" + "\n");

	}

	// one e.g.: ij(A(a), B(a), a=b)
	public void parseJoin(String rulePredicateName, String bodyString) {
		
		String mainString = null;

		if (bodyString.contains("fj")) {
			isFullJoin = true;
		}

		if (bodyString.contains("rj")) {
			isRightJoin = true;
		}
		
		if (bodyString.contains("lj")) {
			isLeftJoin = true;
		}
		
		if (bodyString.contains("ij")) {
			isInnerJoin = true;
		}
		
		// one e.g.: ij(A(a), B(a), a=b)
		mainString = bodyString.substring(2, bodyString.length());

		//(A(a), B(a), a=b)
		String[] s = mainString.split(" ");
        
		for (String subGoal : s) {
			//(A(a),
			if (subGoal.charAt(0) == '(') {
				//A(a)
				parseFactRuleVariables(rulePredicateName, subGoal.substring(1, subGoal.length()-1));
			} else if(subGoal.contains("=")){
				sbBody.append(" ON ");

				// variables check
				// a=b)
				// A.a=B.b;
				//a=b)
				int lastIndex = subGoal.length() - 1;

				String newSubGoal = null;

				// a=b
				if (subGoal.charAt(lastIndex) == ')') {
					newSubGoal = subGoal.substring(0, lastIndex);
				}

				String[] newVariables = newSubGoal.split("\\=");

				// a b
				for (String newVariable : newVariables) {

					for (String factPredicate : factsPredicates) {

						if (factsWithVariables.get(factPredicate) != null) {

							ArrayList<String> realVariables = factsWithVariables.get(factPredicate);

							for (String realVariable : realVariables) {
								if (realVariable.equals(newVariable)) {
									sbBody.append(factPredicate + "." + newVariable + "=");
								}
							}

						}
					}

				}
				
				String finalString = sbBody.toString();
				String newFinalString = finalString;

				if (finalString.charAt(finalString.length() - 1) == '=') {
					newFinalString = finalString.substring(0, finalString.length() - 1) + ";";
				}
				
				sbHeader.append(newFinalString);
				rulesToSQL.add(sbHeader.toString());		

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

				//B(b),
				parseFactRuleVariables(rulePredicateName, subGoal.substring(0, subGoal.length()-1));
		
			}

		}
		
		sbHeader.append(sbBody.toString());
		rulesToSQL.add(sbHeader.toString()+ ";" + "\n");

	}

	// one e.g.: A(a); B(b)
	// for now, we only test 2 tables join
	public void parseUnionOrDifference(String rulePredicateName, String bodyString) {
		String[] subGoals = bodyString.split(" ");
		//A(a); 
		//B(b)
		for(int i = 0; i<subGoals.length; i++) {

			if (subGoals[i].contains(";")) {
				isUnion = true;
			}

			if (subGoals[i].contains("¬")) {
				isDifference = true;
			}

			if (isUnion || isDifference){
			//first table
			if (i == 0) {
				parseFactRuleVariables(rulePredicateName, subGoals[i].substring(0,subGoals[i].length() - 1));

				if (isUnion) {
					sbBody.append(" UNION SELECT ");
				}

				if (isDifference) {
					sbBody.append(" EXCEPT SELECT ");
				}

			} else {
				parseFactRuleVariables(rulePredicateName, subGoals[i]);
			}
			}
		}
		
		sbHeader.append(sbBody.toString());
		rulesToSQL.add(sbHeader.toString()+ ";" + "\n");

	}

	// one e.g.: C('a1','a1')
	public void parseEDBFactsValues(String edbValues) {
        
		String[] s1 = edbValues.split("\\(");

		// facts must exist already, more code to be added ...

		//C
		//'a1','a1')
		String factPredicateName = s1[0];

		boolean factPredicateNameExists = false;
		
		for (String ss : factsPredicates) {
			if (ss.equals(factPredicateName)) {
				factPredicateNameExists = true;
			}
		}

		if (!factPredicateNameExists) {
			factsPredicates.add(factPredicateName);
		}

		//'a1','a1')
		String s2 = s1[1];

		//'a1','a1'
		s2.replaceAll("\\)", "");
		String[] s3 = s2.split(",");

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