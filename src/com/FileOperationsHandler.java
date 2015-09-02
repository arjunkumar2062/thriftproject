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

import org.apache.thrift.TException;









import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;























/**
 * @author arjun
 *
 */
import fileReaderWriter.*;

public class FileOperationsHandler implements FileOperations.Iface {


	public static int numberOfLinesInFile=-1;
	public static List<Integer> lineMemSize=new ArrayList<Integer>();
	@Override
	public void ping() throws TException {
		System.out.println("ping()");
	}

	@Override
	public List<String> readText(Work work) throws InvalidOperation, TException {
		try{
			//writing to b.txt using serverB
			TTransport transport;
			transport = new TSocket("localhost", Constants.PORTB);
			transport.open();
			TProtocol protocol = new  TBinaryProtocol(transport);
			FileOperations.Client client = new FileOperations.Client(protocol);
			String filename = work.filename;
			int numOfLines=work.numOfLines;
			initialize(filename);
			//check if multiple reads need to if numOfLines is large
			int cumByteSize=lineMemSize.get(numberOfLinesInFile);
			int nextIndex=numberOfLinesInFile;
			int linesInFile=numberOfLinesInFile;
			for(int i=linesInFile;i>linesInFile-numOfLines;--i){
				if( i==linesInFile-numOfLines+1 || 
						(cumByteSize-lineMemSize.get(i)<=Constants.THRESHOLDMEMORY && cumByteSize-lineMemSize.get(i-1)>Constants.THRESHOLDMEMORY))
				{
					Work writeWork=new Work();
					writeWork.text=readAndDeleteLastKLinesText(i,nextIndex,filename);
					nextIndex=i-1;
					cumByteSize=lineMemSize.get(numberOfLinesInFile);
					writeWork.filename=Constants.WRITEFILE;
					writeWork.numOfLines=nextIndex-i+1;
					
					UtilityClient.write(client, writeWork);
					initialize(filename);

				}
			}
			transport.close();
			return null;
		}
		catch(TException e){
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		}
		catch(Exception e){
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;		
		}
	}

	@Override
	public boolean writeText(Work work)throws InvalidOperation, TException {
		try{
			List<String> text=work.text;
			String filename=work.filename;
			int numOfLines=work.numOfLines;
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
				return true;
			}


		} catch (IOException e) {
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;		}
		catch(Exception e){
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;		}
		return false;

	}

	private static int findMemorySizeOfData(List<String> text) {
		int numOfBytes=0;
		for (String sCurrentLine : text) {
			numOfBytes=numOfBytes+sCurrentLine.getBytes().length+1;
		}
		return numOfBytes;
	}


	private static void initialize(String filename) throws InvalidOperation{
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
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		}
		catch(IOException e){
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		}
		catch(Exception e){
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		}

	}

	public static List<String> readAndDeleteLastKLinesText(int firstIndex,int lastIndex,String filename) throws InvalidOperation{
		if(lastIndex!=numberOfLinesInFile){
			InvalidOperation io = new InvalidOperation();
			io.why = "LastIndex should be last line of file";
			throw io;
		}
		try {
			File target = new File(filename);
			RandomAccessFile file = new RandomAccessFile(target,"rwd");
			List<String> textlist=contructText(file,firstIndex,lastIndex);
			numberOfLinesInFile=numberOfLinesInFile-lastIndex+firstIndex-1;
			int reducedBytes=lineMemSize.get(lastIndex)-lineMemSize.get(firstIndex-1);
			//concatenates list
			lineMemSize.subList(firstIndex, lastIndex+1).clear();
			file.setLength(target.length()-reducedBytes); 
			file.close();
			return textlist;
		} catch (IOException e) {
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		} 
	}

	public static List<String> readKLinesText(int firstIndex,int lastIndex,String filename) throws InvalidOperation{

		try {
			File target = new File(filename);
			RandomAccessFile file = new RandomAccessFile(target,"rwd");
			List<String> textlist=contructText(file,firstIndex,lastIndex);
			file.close();
			return textlist;
		} catch (IOException e) {
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;
		} 
	}


	public static List<String> contructText(RandomAccessFile file, int firstIndex, int lastIndex) throws InvalidOperation{
		List<String> sb=new ArrayList<String>();
		try {
			for(int i=firstIndex;i<=lastIndex;i++){
				file.seek(lineMemSize.get(i-1));
				String line=file.readLine();
				sb.add(line);
			}
		} catch (IOException e) {
			InvalidOperation io = new InvalidOperation();
			io.why = e.getMessage();
			throw io;		
		}
		return sb;
	}

}
