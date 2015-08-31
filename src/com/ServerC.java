package com;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TServer.Args;

import fileReaderWriter.FileOperations;

public class ServerC {

	public static FileOperationsHandler handler;

	public static FileOperations.Processor processor;

	public static void main(String [] args) {
		try {
			handler = new FileOperationsHandler();
			processor = new FileOperations.Processor(handler);
			int numOfServers=2;
				Runnable simple = new Runnable() {
					public void run() {
						simple(processor);
						
					}
				};      	

				new Thread(simple).start();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void simple(FileOperations.Processor processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(9091);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the simple server...");
			server.serve();
			System.out.println(server.isServing());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
