package com.nian.sqltodatalog;

public enum StatementType {

	// present/save all records returned as a View, so did not check Select/Join/Union/Intersect/Or/And/... statements separately	
	CREATE("Create"), INSERT("Insert"), VIEW("View"), UNKNOW("Unknow");
	
	private final String text;
	
	private StatementType(String s) {
		text = s;
	}
	
	public String toString() {
		return this.text;
	}
	
}
