package com.nian.datalogtosql;

import java.util.ArrayList;

import org.springframework.util.StringUtils;

public class SQLViewSelectBuilder {

	public SQLViewSelectBuilder() {
	}

	public String build() {

		StringBuffer sb = new StringBuffer();
		
		ArrayList<String> rulesToSQL = SaveStringsUtilTwo.getRulesToSQL();
		
		for(String s : rulesToSQL) {
			sb.append(s);
		}

		return sb.toString();
	}

}
