package com.agriculture.psql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/kafka_project";
    private static final String USER = "kafka_user";
    private static final String PASSWORD = "kafka_pass";

    // ✅ Method for HumidityService (String values)
    public static void writeData(String measurement, String field, String value) {
        String sql = "INSERT INTO sensor_data (sensor_type, field, value, timestamp) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, measurement);
            pstmt.setString(2, field);
            pstmt.setString(3, value);
            pstmt.executeUpdate();
            System.out.println("✅ Data saved to PostgreSQL: " + measurement + " - " + field + " = " + value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Method for TemperatureService (Numeric values)
    public static void saveSensorData(String sensorType, double value) {
        String sql = "INSERT INTO sensor_data (sensor_type, field, value, timestamp) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sensorType);
            pstmt.setString(2, "value");
            pstmt.setDouble(3, value);
            pstmt.executeUpdate();
            System.out.println("✅ Data saved to PostgreSQL: " + sensorType + " = " + value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
