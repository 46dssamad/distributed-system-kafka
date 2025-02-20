package com.agriculture.rmi;

import java.rmi.Naming;

public class SoilPhClient {
    public static void main(String[] args) {
        try {
            SoilPhService service = (SoilPhService) Naming.lookup("rmi://localhost/SoilPhService");
            double phValue = service.getSoilPh();
            System.out.println("📡 [CLIENT] Soil pH received from RMI: " + phValue);
        } catch (Exception e) {
            System.err.println("❌ [CLIENT ERROR] Unable to connect to SoilPhService.");
            e.printStackTrace();
        }
    }
}
