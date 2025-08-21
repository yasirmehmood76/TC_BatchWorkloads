package com.example.demo.repo;

import com.example.demo.domain.CUSTOMER;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OracleRepository extends JpaRepository<CUSTOMER, Integer> {
    CUSTOMER findByAccountIdNum(Long accountIdNum);

    Page<CUSTOMER> findAll(Pageable pageable);

    List<CUSTOMER> findByAccountIdNumBetweenOrderByAccountIdNumAsc(Long startId, Long endId);

    List<CUSTOMER> findByRowNumBetweenOrderByRowNumAsc(Integer startId, Integer endId);

    @Query("SELECT MIN(customer.rowNum) FROM CUSTOMER customer")
    Long findMinLineId();

    @Query("SELECT MAX(customer.rowNum) FROM CUSTOMER customer")
    Long findMaxLineId();

    CUSTOMER findByRowNum(Integer rowNum);
}