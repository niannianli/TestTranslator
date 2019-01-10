package com.nian.datalogtosql;

public enum ProgramType {
	//not checked for now
	POSITIVE("Positive"), SEMI_POSITIVE("Semi-Positive"), STRATIFIABLE("Stratifiable"), UNKNOW("Unknow");

	// positive: facts/rules
	// negation: semi_positive or stratification
	// semi_positive: can be expressed as positive, apply negation to EDB only
	// stratifiable: IDB: extension of semi_positive, subgoal must be a head itself,
	// if a new head/rule is defined, we can negate it
	private final String text;

	private ProgramType(String s) {
		text = s;
	}

	public String toString() {
		return this.text;
	}

}
