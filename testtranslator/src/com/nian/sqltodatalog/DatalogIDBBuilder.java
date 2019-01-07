package com.nian.sqltodatalog;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

public class DatalogIDBBuilder {

	protected String input;

	public DatalogIDBBuilder(String input) {
		this.input = input;
	}

	public String build() {

		String[] arr = input.split(" ");

		int fromIndex = 0;

		String viewName = arr[4];

		StringBuilder s = new StringBuilder();
		s.append(viewName + "(");

		String tableName = null;

		if (arr[6].equals("SELECT")) {

			for (int i = 0; i < arr.length - 3; i++) {

				if (arr[i].equals("FROM")) {
					fromIndex = i;
					tableName = arr[i + 1];

					Map<String, ArrayList<String>> mapVariables = SaveStringsUtilOne.getTableNameVariables();

					Iterator it = mapVariables.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						if (pair.getKey().equals(tableName)) {

							ArrayList<String> almv = mapVariables.get(tableName);

							if (arr[7].equals("*")) {

								s.append(String.join(",", almv) + " :- ");

							} else {
								int numberOfVariables = fromIndex - 6 - 1;
								for (int j = 0; j < numberOfVariables; j++) {
									if (j < numberOfVariables - 1) {
										s.append(almv.get(j) + ",");
									} else {
										s.append(almv.get(j) + ")");
									}
								}
							}

							if (arr[i + 2].equals("WHERE")) {

								String[] finalString = (arr[i + 3]).split("\\=\\+\\-", 2);

								for (String str : almv) {
									if (str.equals(finalString[0])) {
										s.append(str + finalString[1]);
									}
								}

								s.append(";");
							} else if (arr[i + 2].equals("JOIN")) {
								String tableNameTwo = arr[i + 4];

								ArrayList<String> almvJoin = mapVariables.get(tableNameTwo);

								s.append("(" + String.join(",", almvJoin) + ",");

								String onCcondition = arr[i + 6];

								s.append(onCcondition + ").");
							}

							else if (arr[i + 2].equals("INNER")) {

								s.append("ij(");

							} else if (arr[i + 2].equals("LEFT")) {

								s.append("lj(");

							} else if (arr[i + 2].equals("RIGHT")) {

								s.append("rj(");

							} else if (arr[i + 2].equals("FULL")) {

								s.append("fj(");

							} else if (arr[i + 2].equals("UNION")) {

								s.append(";");

								// now we only take talbe negation
								int indexOfTableOrViewName = arr.length - 1;

								String tableNameTwo = arr[indexOfTableOrViewName];

								ArrayList<String> almvUnion = mapVariables.get(tableNameTwo);

								s.append(tableNameTwo + "(" + String.join(",", almvUnion) + ".");

							} else if (arr[i + 2].equals("EXCEPT")) {

								s.append(" ¬");

								int indexOfTableOrViewName = arr.length - 1;

								String tableNameTwo = arr[indexOfTableOrViewName];

								ArrayList<String> almvExcept = mapVariables.get(tableNameTwo);

								s.append(tableNameTwo + "(" + String.join(",", almvExcept) + ".");

							}

							break;
						}
						break;
					}

				}

			}
		}

		// to be continued...
		return s.toString() + "\n";

	}
}