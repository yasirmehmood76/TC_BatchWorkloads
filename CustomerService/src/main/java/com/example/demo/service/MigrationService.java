package com.example.demo.service;

import com.example.demo.domain.CUSTOMER;
import com.example.demo.repo.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MigrationService {

    @Autowired
    private OracleRepository oracleRepository;

    public void migrateDataInPartitions(int totalPartitions, int chunkSize) {
        // Get min and max LINE_ID
        Long minId = oracleRepository.findMinLineId();
        Long maxId = oracleRepository.findMaxLineId();

        // Calculate range size for each partition
        long totalRows = maxId - minId + 1;
        long rangeSize = totalRows / totalPartitions;
        System.out.println("Total Rows: " + totalRows);
        System.out.println("Range Size per Partition: " + rangeSize);
        System.out.println("Total Partitions: " + totalPartitions);

        for (int i = 0; i < totalPartitions; i++) {
            long startId = minId + (i * rangeSize);
            long endId = (i == totalPartitions - 1) ? maxId : (startId + rangeSize - 1);

            System.out.println("Partition-" + i + ": " + startId + " to " + endId);

            // Process data in chunks for this partition
            processPartition(startId, endId, chunkSize);
        }
    }

    private void processPartition(long startId, long endId, int chunkSize) {
        long currentStart = startId;

        while (currentStart <= endId) {
            long currentEnd = Math.min(currentStart + chunkSize - 1, endId);

            List<CUSTOMER> lines = oracleRepository.findByAccountIdNumBetweenOrderByAccountIdNumAsc(currentStart, currentEnd);

            // TODO: Process or migrate these lines to Postgres
            System.out.println("Processing chunk: " + currentStart + " to " + currentEnd);

            currentStart = currentEnd + 1;
        }
    }
}
