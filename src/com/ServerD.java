package com;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import fileReaderWriter.FileOperations;

class ServerD implements Runnable {
	private String threadName;
	public static FileOperationsHandler handler;
	public static FileOperations.Processor processor;
	private  static int port;
	ServerD(String name){
		threadName = name;
		System.out.println("Creating " +  threadName );
	}
	public void run() {
		System.out.println("Running " +  threadName );
		handler = new FileOperationsHandler();
		processor = new FileOperations.Processor(handler);
		simple(processor);
	}

	public static void simple(FileOperations.Processor processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the simple server on port = "+port);
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		  port=Constants.PORTA;
	      ServerD A = new ServerD("A");
		  new Thread(A).start();
	     
	      port=Constants.PORTB;
	      ServerD B = new ServerD("B");
		  new Thread(B).start();
	   }
}
