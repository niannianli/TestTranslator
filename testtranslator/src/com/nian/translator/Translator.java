package com.nian.translator;

import com.nian.datalogtosql.DatalogToSQLTranslator;
import com.nian.sqltodatalog.SQLToDatalogTranslator;

public class Translator {

	// need parameter: mapping: another format of input
	public String translateDatalogToSQL(String input) {

		DatalogToSQLTranslator translator = new DatalogToSQLTranslator(input);

		return translator.toSql();
	}

	// seperated, funtion well
	public String translateSQLToDatalog(String input) {

		SQLToDatalogTranslator translator = new SQLToDatalogTranslator(input);

		return translator.toDatalog();
	}

}
