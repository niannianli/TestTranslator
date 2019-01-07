package com.nian.datalogtosql;

public class SQLBuilder {

	public SQLBuilder() {		
	}

	public SQLCreateInsertBuilder createTable(String tableName) {
		return new SQLCreateInsertBuilder(tableName);
	}

	public SQLViewSelectBuilder createViews() {
		return new SQLViewSelectBuilder();
	}

	public String comments(String text) {
		return "/* " + text + " */ \n";
	}

}
