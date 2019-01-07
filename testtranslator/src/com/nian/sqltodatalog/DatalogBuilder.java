package com.nian.sqltodatalog;

public class DatalogBuilder {

	public DatalogBuilder() {		
	}

	public DatalogEDBBuilder createEDB(String input) {
		return new DatalogEDBBuilder(input);
	}

	public DatalogEDBWithValuesBuilder createEDBWithValues(String input) {
		return new DatalogEDBWithValuesBuilder(input);
	}

	public DatalogIDBBuilder createIDB(String input) {
		return new DatalogIDBBuilder(input);
	}

	public String comments(String text) {
		return "/* " + text + " */ \n";
	}

}
