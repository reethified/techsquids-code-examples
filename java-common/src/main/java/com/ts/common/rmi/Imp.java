package com.ts.common.rmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Imp extends UnicastRemoteObject implements Si {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Imp() throws RemoteException, IOException {
		super();
	}

	public String calc(String com) throws IOException {
		String inputLine;
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(com);
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			// pingResult += inputLine;
		}
		return inputLine;
	}

}
