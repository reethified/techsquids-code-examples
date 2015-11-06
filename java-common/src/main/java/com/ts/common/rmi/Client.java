package com.ts.common.rmi;

import java.rmi.*;
import java.util.*;

public class Client {
	public static void main(String args[]) {
		try {
			Si a = (Si) Naming.lookup("rmi://127.0.0.1:9089/ts");
			Scanner s = new Scanner(System.in);
			System.out.println("enter command");
			String com = "ls -ltr /";
			System.out.println(a.calc(com));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
