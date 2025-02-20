package com.agriculture.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class SoilPhServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            SoilPhService service = new SoilPhServiceImpl();
            Naming.rebind("rmi://localhost/SoilPhService", service);
            System.out.println("📡 [LOG] RMI Registry started on port 1099");
            System.out.println("✅ [LOG] SoilPhService RMI started successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
