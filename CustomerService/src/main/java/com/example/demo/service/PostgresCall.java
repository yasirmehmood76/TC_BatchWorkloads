package com.example.demo.service;

import com.example.demo.domain.CUSTOMER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.AbstractDocument;

@Component
public class PostgresCall {

    @Autowired
    RestTemplate restTemplate;

    public long callPostgres(CUSTOMER customer) {
        String url= "http://localhost:8082/api/customer";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<CUSTOMER> customerHttpEntity = new HttpEntity<>(customer, headers);

        long startTime = System.currentTimeMillis();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                customerHttpEntity,
                String.class
        );

        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }
}
