/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com;
import java.io.File;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import fileReaderWriter.*;

public class Client {
	public static void main(String [] args) {

		try {
			TTransport transport;
			transport = new TSocket("localhost", Constants.PORTA);
			transport.open();
			TProtocol protocol = new  TBinaryProtocol(transport);
			FileOperations.Client client = new FileOperations.Client(protocol);
			List<String>text=UtilityClient.read(client);
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		} 
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	
}