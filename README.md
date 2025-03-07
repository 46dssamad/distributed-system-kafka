# Distributed System for Smart Agriculture

This project is a distributed system designed for smart agriculture using **Kafka**, **JAX-RS**, **JAX-WS**, and **RMI**. The system collects sensor data, processes it, and visualizes it using **Grafana**. Data is stored in **PostgreSQL**, and the system supports real-time message streaming with **Kafka**.

## Project Context
In this project, we implemented a distributed system for monitoring environmental conditions in agriculture. The system collects and processes three key sensor measurements:
- **Temperature Monitoring (JAX-RS API):** The RESTful web service is responsible for handling temperature data, allowing real-time updates and retrieval.
- **Humidity Monitoring (JAX-WS SOAP Web Service):** This service provides a structured interface for querying historical and real-time humidity levels.
- **Soil pH Monitoring (RMI Service):** The remote method invocation service manages soil pH readings, enabling remote data analysis and decision-making.

The collected data is stored in a **PostgreSQL** database and visualized using **Grafana** to assist in agricultural decision-making.

## Project Structure
- **JAX-RS Service (REST API):** Handles HTTP requests to fetch and update sensor data in the PostgreSQL database.
- **JAX-WS Service (SOAP Web Service):** Provides operations for querying historical data and generating reports.
- **RMI Service (Remote Method Invocation):** Supports remote function calls for data processing and analytics.
- **Messaging System:** Apache Kafka is used for real-time communication between services.
- **Database:** PostgreSQL is used to store all collected sensor data.
- **Visualization:** Grafana is used to monitor and analyze system data.
- **Scripts:** Shell scripts automate the startup process for services and clients.

## Prerequisites
Before running the project, ensure you have the following installed:
- **Java 17**
- **Apache Kafka**
- **PostgreSQL** (Ensure a database with the required name is created)
- **Grafana**

## Setup Instructions
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/46dssamad/distributed-system-kafka.git
   cd distributed-system-kafka
   ```
2. **Create PostgreSQL Database:**
   - Open PostgreSQL and create a database with the same name as used in the application.
   ```sql
   CREATE DATABASE smart_agriculture;
   ```
3. **Start Kafka:**
   - Start ZooKeeper (if required):
     ```bash
     zookeeper-server-start.sh config/zookeeper.properties
     ```
   - Start Kafka Broker:
     ```bash
     kafka-server-start.sh config/server.properties
     ```
4. **Start Backend Services:**
   - Start the REST API service (JAX-RS)
   - Start the SOAP Web Service (JAX-WS)
   - Start the Remote Method Invocation Service (RMI)
   ```bash
   ./start_services.sh
   ```
5. **Start Client Application:**
   - The client script represents a consumer that interacts with all three services.
   - It sends requests to the REST API, SOAP Web Service, and RMI Service every **3 seconds**.
   ```bash
   ./start_client.sh
   ```
6. **Access Grafana Dashboard:**
   - Open Grafana at `http://localhost:3000`
   - Configure your data source (PostgreSQL)
   - Load the provided dashboard JSON if available.

## Notes
- Ensure the database is correctly configured before running the services.
- Modify the connection settings in the configuration files if needed.
- Logs are stored in the `logs/` directory for debugging.

## Contributing
Feel free to contribute by submitting issues or pull requests.

## Credit
AIT BAKAS Abdessamad

