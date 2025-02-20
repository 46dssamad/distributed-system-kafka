package com.agriculture.rest;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/temperature")
public class TemperatureService {

    private static final String KAFKA_TOPIC = "sensor-data";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/kafka_project";
    private static final String DB_USER = "kafka_user";
    private static final String DB_PASSWORD = "kafka_pass";

    // Vérifier que la classe est bien chargée par Jersey
    static {
        System.out.println("🔄 [LOG] TemperatureService is being loaded by Jersey!");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getTemperature() {
        System.out.println("🔵 [LOG] Received GET request on /api/temperature");

        // Générer une température aléatoire
        Random rand = new Random();
        double temperature = 20 + (rand.nextDouble() * 15); // entre 20 et 35°C
        System.out.println("🌡️ [LOG] Generated temperature: " + temperature + "°C");

        // Enregistrer la température dans PostgreSQL
        saveToDatabase("temperature", temperature);
        System.out.println("🛢️ [LOG] Temperature saved to PostgreSQL");

        // Envoyer la température à Kafka
        sendToKafka("temperature", temperature);
        System.out.println("📡 [LOG] Temperature sent to Kafka");

        return "✅ Temperature recorded: " + temperature + "°C";
    }

    /**
     * Enregistre la température dans PostgreSQL.
     */
    private void saveToDatabase(String sensorType, double value) {
        String sql = "INSERT INTO sensor_data (sensor_type, value, timestamp) VALUES (?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensorType);
            pstmt.setDouble(2, value);
            pstmt.executeUpdate();
            System.out.println("✅ Data saved to PostgreSQL: " + sensorType + " = " + value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie la température à Kafka.
     */
    private void sendToKafka(String sensorType, double value) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            String message = "{\"sensor\":\"" + sensorType + "\", \"value\":\"" + value + "\"}";
            producer.send(new ProducerRecord<>(KAFKA_TOPIC, sensorType, message));
            System.out.println("✅ Data sent to Kafka: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Démarre le serveur Grizzly pour exposer l'API REST.
     */
    public static void main(String[] args) {
        URI baseUri = URI.create("http://localhost:8080/");
        ResourceConfig config = new ResourceConfig().packages("com.agriculture.rest"); // Package corrigé

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);

        try {
            server.start();
            System.out.println("✅ TemperatureService is running on http://localhost:8080/api/temperature");
            Thread.currentThread().join(); // Garde le serveur actif
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
