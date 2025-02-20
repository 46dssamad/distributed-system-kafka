package com.agriculture.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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

public class SoilPhServiceImpl extends UnicastRemoteObject implements SoilPhService {
    private static final long serialVersionUID = 1L;
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/kafka_project";
    private static final String DB_USER = "kafka_user";
    private static final String DB_PASSWORD = "kafka_pass";
    private static final String KAFKA_TOPIC = "sensor-data";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";

    private final Random random = new Random();

    protected SoilPhServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double getSoilPh() throws RemoteException {
        double phValue = 5.0 + (random.nextDouble() * 3.5); // Génère une valeur entre 5.0 et 8.5

        // Enregistrer dans PostgreSQL
        saveToDatabase("soil_ph", phValue);

        // Envoyer à Kafka
        sendToKafka("soil_ph", phValue);

        return phValue;
    }

    private void saveToDatabase(String sensorType, double value) {
        String sql = "INSERT INTO sensor_data (sensor_type, value, timestamp) VALUES (?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensorType);
            pstmt.setDouble(2, value);
            pstmt.executeUpdate();
            System.out.println("✅ [LOG] Soil pH saved to PostgreSQL: " + value);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendToKafka(String sensorType, double value) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            String message = "{\"sensor\":\"" + sensorType + "\", \"value\":" + value + "}";
            producer.send(new ProducerRecord<>(KAFKA_TOPIC, sensorType, message));
            System.out.println("📡 [LOG] Soil pH sent to Kafka: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
