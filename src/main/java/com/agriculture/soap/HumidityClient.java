package com.agriculture.soap;

import jakarta.xml.ws.Service;
import javax.xml.namespace.QName;
import java.net.URL;

public class HumidityClient {
    public static void main(String[] args) {
        try {
            URL wsdlURL = new URL("http://localhost:8081/humidity?wsdl");
            QName qname = new QName("http://soap.agriculture.com/", "HumidityService");
            Service service = Service.create(wsdlURL, qname);
            HumidityService humidityService = service.getPort(HumidityService.class);
            
            String response = humidityService.getHumidity();
            System.out.println("✅ Response from SOAP Server: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
