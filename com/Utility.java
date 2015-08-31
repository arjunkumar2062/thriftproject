package com;

import java.util.List;

import org.apache.thrift.TException;

import fileReaderWriter.FileOperations;
import fileReaderWriter.InvalidOperation;
import fileReaderWriter.Work;

public class Utility {

	public static List<String> read(FileOperations.Client client) throws TException
	{
		client.ping();
		System.out.println("ping()");
		Work work = new Work();
		work.filename=Constants.READFILE;
		work.numOfLines=10;

		try {
			work.text=client.readText(work);
			System.out.println("captured 10 line of text");
			return work.text;
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}
		return null;

	}

	public static boolean write(FileOperations.Client client,Work work) throws TException
	{
		client.ping();
		System.out.println("ping()");

		try {
			//write text into other file
			work.filename=Constants.WRITEFILE;
			return client.writeText(work);
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}
		return false;


	}

	public static void instruction(FileOperations.Client client,String instruction) {
		
	}
}
