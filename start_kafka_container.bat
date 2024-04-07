@echo off

docker run --rm -p 9092:9092 -p 9093:9093 --name kafka-server --hostname kafka-server ^
    -e KAFKA_CFG_NODE_ID=0 ^
    -e KAFKA_CFG_PROCESS_ROLES=controller,broker ^
    -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 ^
    -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 ^
    -e KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT ^
    -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@localhost:9093 ^
    -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER ^
    bitnami/kafka:3.7.0