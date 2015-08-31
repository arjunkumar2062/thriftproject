package com;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TServer.Args;

import fileReaderWriter.FileOperations;

public class ServerB {

	public static FileOperationsHandler handler;

	public static FileOperations.Processor processor;
	public static int port;
	public static void main(String [] args) {
		try {
			handler = new FileOperationsHandler();
			processor = new FileOperations.Processor(handler);
			port=Constants.PORTA;
			Runnable simple = new Runnable() {
				public void run() {
					simple(processor);
				}
			};      	
			Thread A = new Thread(simple);
			A.start();
			port=Constants.PORTB;
			Runnable simple2 = new Runnable() {
				public void run() {
					simple(processor);
				}
			};      	

			Thread B =new Thread(simple2);
			B.start();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void simple(FileOperations.Processor processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the simple server...");
			boolean val=server.isServing();
			System.out.println(val);
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
