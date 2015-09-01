package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TestClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			initialize(Constants.READFILE);
			List<String> text=readKLinesText(1,2,Constants.READFILE);
			String filename=Constants.WRITEFILE;
			int numOfLines=3;
			initialize(filename);
			//find size of text
			int byteSize=findMemorySizeOfData(text);
			
			if(lineMemSize.get(numberOfLinesInFile)+byteSize<=Constants.THRESHOLDMEMORY){
				File file = new File(filename);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				List<String> outputFileText=readAndDeleteLastKLinesText(numberOfLinesInFile-numOfLines+1,numberOfLinesInFile,filename);
				text.addAll(outputFileText);
				for(String line:text){
					bw.write(line+System.lineSeparator());
				}
				bw.close();
			}
			else{
				//create temp file
				File tempFile = new File(Constants.TEMPFILE);
				tempFile.createNewFile();
				FileWriter fw = new FileWriter(tempFile.getAbsoluteFile(),true);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String line:text){
					bw.write(line+System.lineSeparator());
				}
				int prevIndex=1;
				int cumByteSize=0;
				for(int i=0;i<=numberOfLinesInFile;i++){
					if( i==numberOfLinesInFile || 
							(lineMemSize.get(i)-cumByteSize<=Constants.THRESHOLDMEMORY && lineMemSize.get(i+1)-cumByteSize>Constants.THRESHOLDMEMORY))
					{
						text=readKLinesText(prevIndex,i,filename);
						prevIndex=i+1;
						cumByteSize=lineMemSize.get(i);
						for(String line:text){
							bw.write(line+System.lineSeparator());
						}
					}
				}
				tempFile.renameTo(new File(filename));
				bw.close();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static int numberOfLinesInFile=-1;
	public static List<Integer> lineMemSize=new ArrayList<Integer>();

	private static int findMemorySizeOfData(List<String> text) {
		int numOfBytes=0;
		for (String sCurrentLine : text) {
			numOfBytes=numOfBytes+sCurrentLine.getBytes().length+1;
		}
		return numOfBytes;
	}


	private static void initialize(String filename){
		System.out.println("Computing number of lines first time");
		String sCurrentLine;
		int numOfBytes=0;
		BufferedReader br=null;
		int numOfLinesInFile=0;
		lineMemSize.clear();
		try{
			br = new BufferedReader(new FileReader(filename));
			lineMemSize.add(0);
			while ((sCurrentLine = br.readLine()) != null) {
				numOfLinesInFile++;
				numOfBytes=numOfBytes+sCurrentLine.getBytes().length+1;
				lineMemSize.add(numOfBytes);

			}
			br.close();
			numberOfLinesInFile=numOfLinesInFile;
		}
		catch(FileNotFoundException e){

		}
		catch(IOException e){

		}
		catch(Exception e){

		}

	}

	public static List<String> readAndDeleteLastKLinesText(int firstIndex,int lastIndex,String filename){
		if(lastIndex!=numberOfLinesInFile){
			System.err.println("LastIndex should be last line of file");
			return null;
		}
		try {
			File target = new File(filename);
			RandomAccessFile file = new RandomAccessFile(target,"rwd");
			List<String> textlist=contructText(file,firstIndex,lastIndex);
			numberOfLinesInFile=numberOfLinesInFile-lastIndex+firstIndex-1;
			int reducedBytes=lineMemSize.get(lastIndex)-lineMemSize.get(firstIndex-1);
			file.setLength(target.length()-reducedBytes); 
			file.close();
			return textlist;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static List<String> readKLinesText(int firstIndex,int lastIndex,String filename){

		try {
			File target = new File(filename);
			RandomAccessFile file = new RandomAccessFile(target,"rwd");
			List<String> textlist=contructText(file,firstIndex,lastIndex);
			file.close();
			return textlist;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} 
	}


	public static List<String> contructText(RandomAccessFile file, int firstIndex, int lastIndex){
		List<String> sb=new ArrayList<String>();
		try {
			for(int i=firstIndex;i<=lastIndex;i++){
				file.seek(lineMemSize.get(i-1));
				String line=file.readLine();
				sb.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}





}
