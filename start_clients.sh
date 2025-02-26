#!/bin/bash

echo "🚀 Lancement des clients toutes les 3 secondes..."

while true; do
    echo "🔄 [LOG] Exécution des clients..."

    # Consommer le service REST (Température)
    echo "🌡️ [REST] Envoi de la requête..."
    curl -X GET http://localhost:8083/temperature

    echo ""

    # Consommer le service SOAP (Humidité) directement avec XML
    echo "🌧️ [SOAP] Envoi de la requête..."
    curl -X POST -H "Content-Type: text/xml;charset=UTF-8" --data '<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                      xmlns:agri="http://soap.agriculture.com/">
       <soapenv:Header/>
       <soapenv:Body>
          <agri:getHumidity/>
       </soapenv:Body>
    </soapenv:Envelope>' http://localhost:8090/humidity

    echo ""

    # Consommer le service RMI (Soil pH)
    echo "🌱 [RMI] Exécution du client RMI..."
    mvn exec:java -Dexec.mainClass="com.agriculture.rmi.SoilPhClient"

    echo "⏳ Attente de 3 secondes avant la prochaine requête..."
    sleep 3
done
