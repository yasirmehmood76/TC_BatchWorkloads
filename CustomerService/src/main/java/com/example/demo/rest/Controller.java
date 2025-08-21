package com.example.demo.rest;
import com.example.demo.domain.CUSTOMER;
import com.example.demo.domain.RowNumRequest;
import com.example.demo.repo.OracleRepository;
import com.example.demo.service.MigrationService;
import com.example.demo.service.OracleService;
import com.example.demo.service.PostgresCall;
import com.example.demo.service.ProcessTasks;
import com.example.demo.thread.CreateThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    OracleService oracleService;

    @Autowired
    MigrationService migrationService;

    @Autowired
    ProcessTasks processTasks;

    @Autowired
    PostgresCall postgresCall;

    @Autowired
    OracleRepository oracleRepository;

    @PostMapping("/migrate")
    public long createCustomer(@RequestBody RowNumRequest rowNumRequest) throws FileNotFoundException {

//        String threadName = Thread.currentThread().getName();
//        System.out.println("Handling request in thread: " + threadName);
//
//        CUSTOMER customer= oracleRepository.findByRowNum(rowNumRequest.getRowNum());
//        postgresCall.callPostgres(customer);

 //       FileOutputStream fos = new FileOutputStream("D:\\SimulationData\\out.txt", true); // true = append
 //       PrintStream ps = new PrintStream(fos);

        // Redirect standard output to the file
 //       System.setOut(ps);

        long startTime = System.currentTimeMillis();

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = rowNumRequest.getRowNumStart(); i <= rowNumRequest.getRowNumEnd(); i++) {
            CUSTOMER customer = oracleRepository.findByRowNum(i);
            executor.submit(() -> {
                try {
                    long responseTime= postgresCall.callPostgres(customer);
 //                   System.out.println("API Response Time: " + responseTime + " record: " + customer.getRowNum() + " time: " + getElapsedTimeFormatted());
                    System.out.println(getElapsedTimeFormatted()+ "    " + responseTime + "    " + customer.getRowNum());

                } catch (Exception e) {

                }
            });
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(10000, TimeUnit.SECONDS)) {
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


    @GetMapping("/optimal/control")
    public CUSTOMER hello() throws InterruptedException {

        int chunkStart = 1, chunkEnd = 1, chunkSize = 500;
//        List<Integer> threadCount = new ArrayList<>();
//        for (int i = 1; i <= 100; i++) {
//            for (int j = 0; j < 5; j++) {
//                threadCount.add((i));
//            }
//        }
//
//        for (int i = 0; i < 500; i++) {
//            if (i % 5 == 0) {
//                System.out.println("--------------------------------------------");
//            }
//            chunkStart = i * chunkSize + 1;
//            chunkEnd = chunkStart + chunkSize - 1;
//            System.out.println("chunkStart: " + chunkStart + " chunkEnd: " + chunkEnd + " threadCount: " + threadCount.get(i) + " timetaken: " + processTasks.taskProcessor(chunkStart, chunkEnd, threadCount.get(i)));
//        }

//        int threadCountFixed = 5;
//        chunkSize = 10000;
//        for(int i=0; i<10; i++){
//            chunkStart = i * chunkSize + 1;
//            chunkEnd = chunkStart + chunkSize - 1;
//            System.out.println("chunkStart: " + chunkStart + " chunkEnd: " + chunkEnd + " threadCount: " + threadCountFixed + " timetaken: " + processTasks.taskProcessor(chunkStart, chunkEnd, threadCountFixed));
//        }

        adaptiveControl(10000, 900000L, 100, 1, 50);

        return oracleService.getByAccountIdNum(10053L);
    }

    private void adaptiveControl(int totalRecords, long timeLeft, int chunkSize, int minThreadCount, int maxThreadCount) {
        long error = 0;
        long timeTaken = 0;
        int threadCount = minThreadCount;
        int recordsleft = totalRecords;
        int iterations = totalRecords / chunkSize;

        System.out.println("Batch job Started at: " + getElapsedTimeFormatted());

        for (int i = 0; i < iterations; i++) {
            int chunkStart = i * chunkSize + 1;
            int  chunkEnd = chunkStart + chunkSize - 1;
            long timeRequired = timeTaken == 0 ? timeLeft : timeTaken * (iterations - i);
            timeLeft-= timeTaken;
            error = timeRequired - timeLeft;

            recordsleft= totalRecords - chunkStart + 1;

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

            System.out.println("Error: " + error + " k: " + k);

            if(timeLeft < 0) {
                break;
            }

            if (error > 0){
                threadCount= (int) (threadCount + k);
                threadCount = Math.min(threadCount, maxThreadCount);
            } else {
                threadCount= (int) (threadCount + k);
                threadCount = Math.max(threadCount, minThreadCount);
            }
            timeTaken = processTasks.taskProcessor(chunkStart, chunkEnd, threadCount);
            //System.out.println("chunkStart: " + chunkStart + " chunkEnd: " + chunkEnd + " threadCount: " + threadCount + " timetaken: " + timeTaken + " recordsleft: "+ recordsleft + " timeleft: " + timeLeft);
            System.out.println(" threadCount:  " + threadCount + " time:  "+ getElapsedTimeFormatted() + " timetaken: " + timeTaken + " recordsleft: "+ recordsleft + " timeleft: " + timeLeft);

        }

        System.out.println("Batch job ended at: " + getElapsedTimeFormatted());
    }

    private static final long START_TIME = System.currentTimeMillis();

    public static long getElapsedMillis() {
        return System.currentTimeMillis() - START_TIME;
    }

    public static String getElapsedTimeFormatted() {
        long elapsed = getElapsedMillis();
        return String.format("%.3f", elapsed / 1000.0);
    }
}