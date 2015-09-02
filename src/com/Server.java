package com;

import org.apache.commons.logging.Log;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.server.TServer.Args;

import fileReaderWriter.FileOperations;
import fileReaderWriter.InvalidOperation;

public class Server {

	public static FileOperationsHandler handler;
	public static FileOperations.Processor processor;

	static class RunnableServer implements Runnable {
		public RunnableServer(TServer svr) {
			this.server = svr;
		}

		@Override
		public void run() {
			System.out.println("Starting simple server");
			server.serve();
		}

		private TServer server;
	}
	public static void main(String [] args) throws InvalidOperation {
		TServer A=startServer(Constants.PORTA);
		TServer B=startServer(Constants.PORTB);
		
	}

	public static TServer startServer(int portNumber) throws InvalidOperation{
		handler = new FileOperationsHandler();
		processor = new FileOperations.Processor(handler);
		TServerTransport serverTransport;
		try {
			serverTransport = new TServerSocket(portNumber);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			RunnableServer simple = new RunnableServer(server);
			Thread serverThread = new Thread(simple);
			serverThread.start();
			return server;
		} catch (TTransportException e) {
			InvalidOperation io = new InvalidOperation();
		    io.why = e.getMessage();
		    throw io;
		}
	}

}
