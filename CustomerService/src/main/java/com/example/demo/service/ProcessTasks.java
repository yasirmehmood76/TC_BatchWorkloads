package com.example.demo.service;

import com.example.demo.domain.CUSTOMER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessTasks {

    @Autowired
    OracleService oracleService;

    @Autowired
    PostgresCall postgresCall;

    public long taskProcessor(int chunkStart, int chunkEnd, int threadCount) {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<CUSTOMER> customers= oracleService.getByRowNumBetween(chunkStart,chunkEnd);

        for (int i = 0; i <= customers.size()-1; i++) {
            CUSTOMER customer = customers.get(i);
            executor.submit(() -> {
                try {
                    postgresCall.callPostgres(customer);
                } catch (Exception e) {

                }
            });
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(100, TimeUnit.SECONDS)) {
                System.out.println("Some tasks are still running. Forcing shutdown...");
                executor.shutdownNow(); // Force shutdown
            } else {
            //    System.out.println("All tasks completed. Executor is shutdown.");
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted. Forcing shutdown...");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}

