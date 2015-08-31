package com;

import org.apache.thrift.server.TServer;

public class ThreadServerCouple {

	Thread thread;
	TServer server;
	ThreadServerCouple(Thread thread, TServer server){
		this.thread=thread;
		this.server=server;
	}
	ThreadServerCouple(){
		
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	public TServer getServer() {
		return server;
	}
	public void setServer(TServer server) {
		this.server = server;
	}
	
}
