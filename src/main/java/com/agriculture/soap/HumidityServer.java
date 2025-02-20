package com.agriculture.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import jakarta.xml.ws.Endpoint;

import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebService
public class HumidityServer {

    private static final String KAFKA_TOPIC = "sensor-data";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/kafka_project";
    private static final String DB_USER = "kafka_user";
    private static final String DB_PASSWORD = "kafka_pass";

    @WebMethod
    public String getHumidity() {
        String humidity = "75%";
        System.out.println("🔵 [LOG] Received SOAP request for Humidity");
        
        // Enregistrer dans PostgreSQL
        saveToDatabase("humidity", 75.0);

        // Envoyer à Kafka
        sendToKafka("humidity", 75.0);

        return "Humidité du sol : " + humidity;
    }

    private void saveToDatabase(String sensorType, double value) {
        String sql = "INSERT INTO sensor_data (sensor_type, value, timestamp) VALUES (?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensorType);
            pstmt.setDouble(2, value);
            pstmt.executeUpdate();
            System.out.println("✅ [LOG] Humidity data saved to PostgreSQL!");

        } catch (SQLException e) {
            System.err.println("❌ [ERROR] Failed to save humidity data to PostgreSQL!");
            e.printStackTrace();
        }
    }

    private void sendToKafka(String sensorType, double value) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            String message = "{\"sensor\":\"" + sensorType + "\", \"value\":\"" + value + "\"}";
            producer.send(new ProducerRecord<>(KAFKA_TOPIC, sensorType, message));
            System.out.println("📡 [LOG] Data sent to Kafka: " + message);
        } catch (Exception e) {
            System.err.println("❌ [ERROR] Failed to send data to Kafka!");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) { // 👈 Correction de la signature de la méthode `main`
        try {
            // Définition de l'URL du service SOAP
            String url = "http://localhost:8081/humidity";
            
            // Log de démarrage
            System.out.println("🔄 [LOG] Starting SOAP HumidityService...");
            
            // Publication du service SOAP
            Endpoint.publish(url, new HumidityService());
            
            // Log de confirmation du démarrage
            System.out.println("✅ [LOG] SOAP Service is running at " + url);
            System.out.println("📡 [LOG] WSDL available at " + url + "?wsdl");

        } catch (Exception e) {
            System.err.println("❌ [ERROR] Failed to start HumidityServer!");
            e.printStackTrace();
        }
    }

}
