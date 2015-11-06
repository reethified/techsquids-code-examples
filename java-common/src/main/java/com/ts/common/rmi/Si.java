package com.ts.common.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Si extends Remote {
	public String calc(String com) throws RemoteException,IOException;
}
