package com.nian.datalogtosql;

//Datalog to SQL
public class DatalogToSQLTranslator {

	// evaluate/parse datalog program
	DatalogProgram datalogProgram;
	String input;

	public DatalogToSQLTranslator(String input) {
		this.input = input;
		this.datalogProgram = new DatalogProgram(input);
	}

	// output
	StringBuilder sb = new StringBuilder();

	public String toSql() {

		//no evaluate for now
		//parse first
		//save all info from Datalog
		evaluateParseSave();

		// then output SQL statements
		SQLBuilder sqlBuilder = new SQLBuilder();

		String[] strings = input.replaceAll("\n","").split(".");

		//for each fact or rule
		for (String s : strings) {
			if (s.contains(":-")) {
				//PROJECTION_RULE
				//a) :- A
				//a).
				
				String[] ss = s.split("\\(");
			}else {
				//C
				//'a2','b2'). 
				String[] ssss = s.split("\\(");
				sb.append(sqlBuilder.createTable(ssss[0]).build());
			}
		}

		sb.append(sqlBuilder.createViews().build());
		return sb.toString();
	}

	//no evaluate for now
	//only parse
	public void evaluateParseSave() {

		ProgramType type = datalogProgram.getProgramType();
		ProgramEvaluatorParser evaluatorParser = new ProgramEvaluatorParser(datalogProgram);

		//for all types, do the same thing for now
		//more work to be done here later
		if (type == ProgramType.POSITIVE) {
			
			evaluatorParser.parse(input);
		} else if (type == ProgramType.SEMI_POSITIVE || type == ProgramType.STRATIFIABLE) {
			// not working for this part, assume all rules are correct/safe
			
			// StratifiedDatalogProgram stratification = datalogProgram.stratify();
			// evaluator.stratifiedEvaluation(stratification);
			//parse is the last step
			
			evaluatorParser.parse(input);
		} else {
			// "Error: could not compute program type";
			
			//assume all rules are safe now
			evaluatorParser.parse(input);
		}
	}

	// used: positive; negation: semi-positive(EDB)/stratification(IDB)
	public String getProgramType() {
		return datalogProgram.getProgramType().toString();
	}

	// make sure rules are safe, to be used later
	/*
	 * public String stratify() {
	 * 
	 * ProgramType type = datalogProgram.getProgramType();
	 * 
	 * if (type == ProgramType.POSITIVE) { return
	 * "Program is Positive, no need to stratify it"; } else if (type ==
	 * ProgramType.SEMI_POSITIVE || type == ProgramType.STRATIFIABLE) { return
	 * datalogProgram.stratify().toString(); } else { return
	 * "Program is not Stratifiable"; } }
	 */

	// to be used later
/*	private boolean isRecursive(Tgd t) {
		for (Literal l : t.getLeft()) {
			if (l.getAtom().getName().equals(t.getRight().getName())) {
				return true;
			}
		}
		return false;
	}*/

}