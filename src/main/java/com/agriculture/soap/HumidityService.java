package com.agriculture.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@WebService
public class HumidityService {

    private static final String KAFKA_TOPIC = "sensor-data";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/kafka_project";
    private static final String DB_USER = "kafka_user";
    private static final String DB_PASSWORD = "kafka_pass";

    @WebMethod
    public String getHumidity() {
        // 🔄 Générer une valeur d'humidité aléatoire entre 50% et 100%
        Random rand = new Random();
        double humidity = 50 + (rand.nextDouble() * 50); // Valeur entre 50 et 100

        System.out.println("🔵 [LOG] Received SOAP request for Humidity");
        System.out.println("🌧️ [LOG] Generated Humidity: " + humidity + "%");

        // Enregistrer dans PostgreSQL
        saveToDatabase("humidity", humidity);

        // Envoyer à Kafka
        sendToKafka("humidity", humidity);

        return "Humidité du sol : " + humidity + "%";
    }

    private void saveToDatabase(String sensorType, double value) {
        String sql = "INSERT INTO sensor_data (sensor_type, value, timestamp) VALUES (?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensorType);
            pstmt.setDouble(2, value);
            pstmt.executeUpdate();
            System.out.println("✅ [LOG] Humidity data saved to PostgreSQL: " + value + "%");

        } catch (SQLException e) {
            System.err.println("❌ [ERROR] Failed to save humidity data to PostgreSQL!");
            e.printStackTrace();
        }
    }

    private void sendToKafka(String sensorType, double value) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            String message = "{\"sensor\":\"" + sensorType + "\", \"value\":\"" + value + "\"}";
            ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, sensorType, message);

            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("📡 [LOG] Data sent to Kafka: " + message);
                } else {
                    System.err.println("❌ [ERROR] Failed to send data to Kafka!");
                    exception.printStackTrace();
                }
            });

            producer.flush();
        } catch (Exception e) {
            System.err.println("❌ [ERROR] Exception while sending data to Kafka!");
            e.printStackTrace();
        }
    }
}
