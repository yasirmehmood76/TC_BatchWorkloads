package com.example.demo.kafka;

import com.example.demo.domain.Customer;
import com.example.demo.service.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    DatabaseService databaseService;

    @KafkaListener(topics = "oracle-db", groupId = "my-group", autoStartup = "false")
    public void consume(String message) {
        logger.info("Received raw Kafka message: {}", message);

        try {
            Map<String, Object> map = objectMapper.readValue(message, Map.class);

            // Extract payload map and convert to JSON string
            Object payloadObj = map.get("payload");
            String payloadJson = objectMapper.writeValueAsString(payloadObj);
            logger.info("Parsed Customer object: {}", payloadJson);

            // Map to Customer class
            Customer customer = objectMapper.readValue(payloadJson, Customer.class);
            logger.info("Parsed Customer object: {}", customer.toString());
            logger.info("Parsed Customer object: {}", customer.getAccountId());

            databaseService.saveCustomer(customer);

        } catch (Exception e) {
            logger.error("Failed to parse JSON message", e);
        }
    }
}
