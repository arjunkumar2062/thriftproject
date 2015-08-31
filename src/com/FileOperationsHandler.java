package com;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;









/**
 * @author arjun
 *
 */
import fileReaderWriter.*;

public class FileOperationsHandler implements FileOperations.Iface {

	@Override
	public void ping() throws TException {
		System.out.println("ping()");
	}

	@Override
	public List<String> readText(Work work) throws InvalidOperation, TException {
		try{
			List<String> text;
			String filename = work.filename;
			int numOfLines=work.numOfLines;
			text = readLastKLines(numOfLines,filename);
			return text;
		}
		catch(Exception e){
			return null;
		}
	}

	@Override
	public boolean writeText(Work work)throws InvalidOperation, TException {
		try{
			List<String> text=new ArrayList<String>();
			text=work.text;
			//write operation
			File file = new File(work.filename);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(String line:text){
				bw.write(line+System.lineSeparator());
			}
			bw.close();
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean writeAtBeginning(String filename, List<String> text){
		return false;
	}
	public List<String> readLastKLines(int numOfLines,String filename){
		List<String> text=new ArrayList<String>();
		//compute number of lines in text file
		BufferedReader br = null;
		int numOfLinesInFile=0;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) {
				numOfLinesInFile++;
			}
			br.close();
			int i=0;
			br = new BufferedReader(new FileReader(filename));
			while ((sCurrentLine = br.readLine()) != null) {
				i++;
				if(i<=numOfLinesInFile-numOfLines)
					continue;
				else{
					text.add(sCurrentLine);
				}
			}
			br.close();
			return text;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}

}