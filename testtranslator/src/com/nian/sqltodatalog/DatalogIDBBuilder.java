package com.nian.sqltodatalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class DatalogIDBBuilder {

	protected String input;

	public DatalogIDBBuilder(String input) {
		this.input = input;
	}

	public String build() {

		String[] arr = input.split(" ");

		String viewName = arr[4];

		StringBuilder sbHeader = new StringBuilder();
		sbHeader.append(viewName + "(");
		
		String tableName = arr[9].replaceAll("\\;", "");
		
		StringBuilder sbBody = new StringBuilder();
					sbBody.append(tableName + "(");
				
				//meaning we have one variable symbol, which is *
				if(arr[7].equals("*")) {
					ArrayList<String> variables = SaveStringsUtilOne.getTableNameVariables().get(tableName);
					
					//variables must exist already
					sbHeader.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
					sbBody.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()));
				}else {
					ArrayList<String> viewVariables = new ArrayList<String>();
					String[] s = arr[7].split(",");
					sbHeader.append(StringUtils.arrayToCommaDelimitedString(s));
					sbBody.append(StringUtils.arrayToCommaDelimitedString(s));
					for(String viewVariable: s) {
						viewVariables.add(viewVariable);
					}
					
					Map<String, ArrayList<String>> viewVariablesMap = new HashMap<String, ArrayList<String>>();
					viewVariablesMap.put(viewName, viewVariables);						
				}
				
				boolean isInnerJoin = false;
				boolean isLeftJoin = false;
				boolean isRightJoin = false;
				boolean isFullJoin = false;
				
				if(arr.length<=10) {
					sbBody.append(")");
				}else {
				
                if(arr[10].equals("WHERE")){
                	sbBody.append("), ");
                	sbBody.append(arr[11]);
                }
                
                else if(arr[11].equals("JOIN")) {
                	
                	if(arr[10].equals("INNER")) {
                		isInnerJoin = true;
                	}	else if(arr[10].equals("LEFT")) {
                		isLeftJoin = true;
                	}else if(arr[10].equals("RIGHT")) {
                		isRightJoin = true;
                	}else if(arr[10].equals("FULL")) {
                		isFullJoin = true;
                	}
                	
                		String tableNameTwo = arr[12];
                		sbBody.append("), " + tableNameTwo+"(");
                		
                		ArrayList<String> variables = SaveStringsUtilOne.getTableNameVariables().get(tableNameTwo);
                		if(arr[7].equals("*")) {
                		sbHeader.append(","+StringUtils.arrayToCommaDelimitedString(variables.toArray()));
                		}
                		sbBody.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()) + "), ");
                		
                		//A.a=B.b
                		String s = arr[14];
                		//a=b)
                		String[] news = s.split("\\=");
                		StringBuilder sb = new StringBuilder();
                		for(String subs : news) {
                			sb.append(subs.substring(2,subs.length()) + "=");
                		}
                		
                		String lastString = sb.toString().substring(0,sb.toString().length()-1);
                		sbBody.append(lastString + ")");
                }
                	
                else if(arr[10].equals("UNION")) {
                	sbBody.append("); ");
                	String tableNameTwo = arr[14];
                	sbBody.append(tableNameTwo+"(");
                	if(arr[12].equals("*")) {
                		ArrayList<String> variables = SaveStringsUtilOne.getTableNameVariables().get(tableNameTwo);
    					
    					//variables must exist already
                		sbHeader.append(","+StringUtils.arrayToCommaDelimitedString(variables.toArray()));
    					sbBody.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()) + ")");
                	}else {
    					String[] s = arr[12].split(",");
    					sbHeader.append(","+StringUtils.arrayToCommaDelimitedString(s));
    					sbBody.append(StringUtils.arrayToCommaDelimitedString(s) + ")");
                	}
                }
                
                else if(arr[10].equals("EXCEPT")) {
                	sbBody.append("), ¬");
                	String tableNameTwo = arr[14];
                	sbBody.append(tableNameTwo+"(");
                	if(arr[12].equals("*")) {
                		ArrayList<String> variables = SaveStringsUtilOne.getTableNameVariables().get(tableNameTwo);
    					
    					//variables must exist already
    					sbBody.append(StringUtils.arrayToCommaDelimitedString(variables.toArray()) + ")");
                	}else {
    					String[] s = arr[12].split(",");
    					sbBody.append(StringUtils.arrayToCommaDelimitedString(s) + ")");
                	}
                }
				}
				
	    //end of header
        sbHeader.append(")");
        //rule sign
        sbHeader.append(" :- ");
        if(isInnerJoin) {
        	sbHeader.append("ij(");
        }
        if(isLeftJoin) {
        	sbHeader.append("lj(");
        }
        if(isRightJoin) {
        	sbHeader.append("rj(");
        }
        if(isFullJoin) {
        	sbHeader.append("fj(");
        }
        //connect to body
        sbHeader.append(sbBody.toString());
		//end of rule
		return sbHeader.toString() + "." + "\n";
}
}