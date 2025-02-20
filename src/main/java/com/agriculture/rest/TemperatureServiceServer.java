package com.agriculture.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import java.net.URI;

public class TemperatureServiceServer {
    public static void main(String[] args) {
        // Définir l'URI de base
        URI baseUri = URI.create("http://localhost:8080/");

        // Configurer Jersey pour scanner le package contenant les services REST
        ResourceConfig config = new ResourceConfig().packages("com.agriculture.rest");

        // Démarrer le serveur Grizzly
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        try {
            server.start();
            System.out.println("✅ TemperatureService is running on http://localhost:8080/api/temperature");
            Thread.currentThread().join(); // Maintenir le serveur actif
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
