package com.example.demo.kafka;

import com.example.demo.domain.Customer;
import com.example.demo.service.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class BatchConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BatchConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> buffer = new LinkedList<>();
    private static final long START_TIME = System.currentTimeMillis();
    private static final int CHUNK_SIZE = 100000;
    private static final int INITIAL_THREAD_COUNT = 1; // Initial thread count
    private static final int MIN_THREAD_COUNT = 1;
    private static final int MAX_THREAD_COUNT = 48;
    private static final int TOTAL_RECORDS = 9000000;
    private static final int MAX_TIME = 600000; // Maximum time in milliseconds to process all records
    private long timeLeft = MAX_TIME;
    private int threadCount = INITIAL_THREAD_COUNT;
    private int recordsleft = TOTAL_RECORDS;
    private int iterations = TOTAL_RECORDS / CHUNK_SIZE;
    private long error = 0;
    private long timeTaken = 0;
    private int i=0;
    private int messagesReceived = 0;

    @Autowired
    DatabaseService databaseService;

    @KafkaListener(
            topics = "oracle-db",
            groupId = "my-group",
            containerFactory = "kafkaListenerContainerFactory",
            autoStartup = "true"
    )
    private void adaptiveControl(List<String> messages) {
        messagesReceived += messages.size();
        buffer.addAll(messages);
        //logger.info("messages added to the buffer: " + buffer.size() + " messages received: " + messagesReceived);
        while (buffer.size() >= CHUNK_SIZE && recordsleft > 0) {
            List<String> batch = new ArrayList<>(buffer.subList(0, CHUNK_SIZE));
            buffer.subList(0, CHUNK_SIZE).clear();
            int chunkStart = i * CHUNK_SIZE + 1;
            int  chunkEnd = chunkStart + CHUNK_SIZE - 1;
            long timeRequired = timeTaken == 0 ? timeLeft : timeTaken * (iterations - i);
            i++;
            timeLeft-= timeTaken;
            error = timeRequired - timeLeft;

            recordsleft= TOTAL_RECORDS - chunkStart + 1;

            //   timeTaken = processTasks.taskProcessor(chunkStart, chunkEnd, threadCount);
            long k=0;
            if(timeTaken!=0){
                k = (error/(timeTaken / threadCount));
                if (k > 1) {
                    k = 1; // Limit the adjustment to a maximum of 3 threads
                } else if (k < -1) {
                    k = -1; // Limit the adjustment to a minimum of -3 threads
                }
            }

//            if(Math.abs(error)<timeTaken){
//                k = 0;
//            }

            //System.out.println("Error: " + error + " k: " + k);

            if(timeLeft < 0) {
                break;
            }

            if (error > 0){
                threadCount= (int) (threadCount + k);
                threadCount = Math.min(threadCount, MAX_THREAD_COUNT);
            } else {
                threadCount= (int) (threadCount + k);
                threadCount = Math.max(threadCount, MIN_THREAD_COUNT);
            }
            timeTaken = processBatch(batch, threadCount);
            //System.out.println("chunkStart: " + chunkStart + " chunkEnd: " + chunkEnd + " threadCount: " + threadCount + " timetaken: " + timeTaken + " recordsleft: "+ recordsleft + " timeleft: " + timeLeft);
            System.out.println(" threadCount:  " + threadCount + " time:  "+ getElapsedTimeFormatted() + " timetaken: " + timeTaken + " recordsleft: "+ recordsleft + " timeleft: " + timeLeft);
        }
    }

    private long processBatch(List<String> batch, int threadCount) {
        List<Customer> customers = new ArrayList<>();
        for (String message : batch) {
            //logger.info("Processing message: {}", message);
            Customer customer = messageToCustomer(message);
            if (customer != null) {
                customers.add(customer);
            }
        }

        return saveCustomers(customers, threadCount);
    }

    private Customer messageToCustomer(String message) {
        try{
            Map<String, Object> map = objectMapper.readValue(message, Map.class);
            Object payloadObj = map.get("payload");
            String payloadJson = objectMapper.writeValueAsString(payloadObj);
            //logger.info("Parsed Customer object: {}", payloadJson);

            // Map to Customer class
            Customer customer = objectMapper.readValue(payloadJson, Customer.class);
            //logger.info("Parsed Customer object: {}", customer.toString());
            //logger.info("Parsed Customer object: {}", customer.getAccountId());
            return customer; // Placeholder
        } catch(Exception e){
            logger.error("Failed to parse JSON message", e);
        }
        return null;
    }


    private long saveCustomers(List<Customer> customers, int threadCount) {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i <= customers.size()-1; i++) {
            Customer customer = customers.get(i);
            executor.submit(() -> {
                try {
                    databaseService.saveCustomer(customer);
                } catch (Exception e) {

                }
            });
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(10000, TimeUnit.SECONDS)) {
                logger.error("Some tasks are still running. Forcing shutdown...");
                executor.shutdownNow(); // Force shutdown
            } else {
                //    System.out.println("All tasks completed. Executor is shutdown.");
            }
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted. Forcing shutdown...");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    public static long getElapsedMillis() {
        return System.currentTimeMillis() - START_TIME;
    }

    public static String getElapsedTimeFormatted() {
        long elapsed = getElapsedMillis();
        return String.format("%.3f", elapsed / 1000.0);
    }
}
