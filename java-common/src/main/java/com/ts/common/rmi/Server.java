package com.ts.common.rmi;

import java.rmi.Naming;

public class Server {
	public static void main(String args[]) {
		try {
			 System.setProperty("java.rmi.server.hostname", "localhost");
			Si stub = new Imp();
			Naming.rebind("rmi://localhost:9089/ts", stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}