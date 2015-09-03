package com;

import java.util.List;

import org.apache.thrift.TException;

import fileReaderWriter.FileOperations;
import fileReaderWriter.InvalidOperation;
import fileReaderWriter.Work;

public class UtilityClient {

	public static List<String> read(FileOperations.Client client) throws TException
	{
		client.ping();
		System.out.println("ping()");
		Work work = new Work();
		work.filename=Constants.READFILE;
		work.numOfLines=Constants.NUMOFLINES;
		try {
			work.text=client.readText(work);
			System.out.println("captured "+Constants.NUMOFLINES+" line of text");
			return work.text;
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return null;

	}

	public static boolean write(FileOperations.Client client,Work work) throws TException
	{
		client.ping();
		System.out.println("ping()");

		try {
			//write text into other file
			boolean success=client.writeText(work);
			System.out.println(success);
			return success;
		} catch (InvalidOperation io) {
			System.out.println("Invalid operation: " + io.why);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		return false;


	}
	
	

}
