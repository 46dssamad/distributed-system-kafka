package com.agriculture.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SoilPhService extends Remote {
    double getSoilPh() throws RemoteException;
}
