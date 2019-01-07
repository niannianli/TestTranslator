package com.nian.translator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TranslatorStart {

	//get input file
	//generate output file
	public static void main(String[] args) {

		Boolean inputDatalog = false;
		Boolean inputSQL = false;

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;

		StringBuffer stringBuffer = null;
		String output = null;

		BufferedWriter writer = null;

		try {
			File file = new File(args[0]);
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			
			stringBuffer = new StringBuffer();
			String line;
			
			while ((line = bufferedReader.readLine()) != null) {
				if (line.equals("Datalog")) {
					inputDatalog = true;
					continue;
				} else if (line.equals("SQL")) {
					inputSQL = true;
					continue;
				} else {
				}
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				fileReader.close();
			} catch (Exception ex) {
			}

			String input = stringBuffer.toString();
			
			Translator translator = new Translator();

			if (inputSQL) {
				
				output = translator.translateSQLToDatalog(input);
			} else if (inputDatalog) {

				output = translator.translateDatalogToSQL(input);
			}
		}

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[0]+"Output"), "utf-8"));
			writer.write(output);
		} catch (IOException ex) {
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}
}