package com.example.demo.service;

import com.example.demo.domain.CUSTOMER;
import com.example.demo.repo.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OracleService {

    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    PostgresCall postgresCall;

    public CUSTOMER getByAccountIdNum(Long accountIdNum) {
        return oracleRepository.findByAccountIdNum(accountIdNum);
    }

    public void fetchLinesInChunks(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("accountIdNum").ascending());
        Page<CUSTOMER> page = oracleRepository.findAll(pageable);

        List<CUSTOMER> customers = page.getContent();

        customers.forEach(customer -> {
            System.out.println("ACCOUNT_ID_NUM: " + customer.getAccountIdNum());
        });

        System.out.println("Is last page? " + page.isLast());
    }

    public void getByAccountIdNumBetween(Long startId, Long endId) {
        List<CUSTOMER> customers = oracleRepository.findByAccountIdNumBetweenOrderByAccountIdNumAsc(startId, endId);

        customers.forEach(customer -> {
            System.out.println("ACCOUNT_ID_NUM: " + customer.getAccountIdNum());
        });

    }

    public List<CUSTOMER> getByRowNumBetween(Integer startId, Integer endId) {

//        customers.forEach(customer -> {
//            postgresCall.callPostgres(customer);
//            System.out.println("ROW_NUM: " + customer.getRowNum());
//        });

        return oracleRepository.findByRowNumBetweenOrderByRowNumAsc(startId, endId);
    }
}
