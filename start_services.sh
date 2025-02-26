#!/bin/bash


# Démarrer le service RMI
echo "🔵 Démarrage du service RMI Soil pH..."
mvn exec:java -Dexec.mainClass="com.agriculture.rmi.SoilPhServiceImpl" &

# Démarrer le service REST
echo "🔵 Démarrage du service REST Temperature..."
mvn exec:java -Dexec.mainClass="com.agriculture.rest.TemperatureServiceServer" &

# Démarrer le service SOAP
echo "🔵 Démarrage du service SOAP Humidity..."
mvn exec:java -Dexec.mainClass="com.agriculture.soap.HumidityServiceImpl" &

echo "✅ Tous les services sont démarrés avec succès."

