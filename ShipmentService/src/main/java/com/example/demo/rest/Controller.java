package com.example.demo.rest;
import com.example.demo.domain.Customer;
import com.example.demo.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    DatabaseService databaseService;

    @GetMapping("/hello")
    public String hello() {

        Customer customer = new Customer();
        customer.setAccountId(123L);
        customer.setUserName("testUser");

        databaseService.saveCustomer(customer);

        return "Hello, World!";
    }


    @PostMapping("/customer")
    public String createCustomer(@RequestBody Customer customer) throws InterruptedException {

       String threadName = Thread.currentThread().getName();
  //    System.out.println("Handling request in thread: " + threadName);

        databaseService.saveCustomer(customer);
   //     Thread.sleep(100);
   //     doCpuWork100ms();
        return "Customer created successfully!";
    }

    public static void doCpuWork100ms() {
        long start = System.nanoTime();
        long duration = 100_000_000L; // 100 milliseconds

        while (System.nanoTime() - start < duration) {
            double dummy = Math.sqrt(9876.54321); // Prevent optimization
        }

    //    System.out.println("Finished 100 ms of busy CPU work");
    }

}