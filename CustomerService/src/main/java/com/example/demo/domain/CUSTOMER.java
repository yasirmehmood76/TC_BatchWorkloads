package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CUSTOMER_STAGING")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CUSTOMER {
    @Id
    @Column(name = "ROW_NUM")
    private Integer rowNum;

    @Column(name = "ACCOUNT_ID_NUM")
    private Long accountIdNum;

    @Column(name = "ACCOUNT_ID")
    @JsonProperty("ACCOUNT_ID")
    private String accountId;

    @Column(name = "USER_NAME")
    private String userName;
}