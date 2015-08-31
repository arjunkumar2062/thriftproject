package com;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.server.TServer.Args;

import fileReaderWriter.FileOperations;

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
	public static void main(String [] args) {
		ThreadServerCouple A=startServer(Constants.PORTA);
		ThreadServerCouple B=startServer(Constants.PORTB);
		TServer serverA=A.server;
		TServer serverB=B.server;
		//serverA.stop();
	}

	public static ThreadServerCouple startServer(int portNumber){
		handler = new FileOperationsHandler();
		processor = new FileOperations.Processor(handler);
		TServerTransport serverTransport;
		try {
			serverTransport = new TServerSocket(portNumber);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			RunnableServer simple = new RunnableServer(server);
			Thread serverThread = new Thread(simple);
			serverThread.start();
			return new ThreadServerCouple(serverThread,server);
		} catch (TTransportException e) {
			e.printStackTrace();
			return null;
		}
	}

}
