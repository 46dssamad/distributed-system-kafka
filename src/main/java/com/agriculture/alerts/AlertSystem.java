package com.agriculture.alerts;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AlertSystem {
    private static final String KAFKA_TOPIC = "sensor-data";
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "alert-system";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
            System.out.println("🚨 [LOG] AlertSystem is monitoring sensor data...");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                records.forEach(record -> {
                    System.out.println("🔍 [LOG] Received data: " + record.value());

                    // Détection d'une température trop élevée
                    if (record.value().contains("temperature")) {
                        double temp = extractValue(record.value());
                        if (temp > 35.0) {
                            System.out.println("🚨 [ALERT] High temperature detected: " + temp + "°C!");
                        }
                    }

                    // Détection d'une humidité trop basse
                    if (record.value().contains("humidity")) {
                        double humidity = extractValue(record.value());
                        if (humidity < 30.0) {
                            System.out.println("🚨 [ALERT] Low humidity detected: " + humidity + "%!");
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("❌ [ERROR] AlertSystem failed!");
            e.printStackTrace();
        }
    }

    private static double extractValue(String message) {
        try {
            String valueStr = message.replaceAll("[^0-9.]", ""); // Extraire uniquement les chiffres
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            return -1;
        }
    }
}
