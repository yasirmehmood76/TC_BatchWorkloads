package com.example.demo.domain;

import com.example.demo.utility.StringToLongDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    @Id
    @JsonProperty("ACCOUNT_ID")
    @Column(name = "ACCOUNT_ID")
    @JsonDeserialize(using = StringToLongDeserializer.class)
    private Long accountId;

    @JsonProperty("SOLO_MOBILE_ACCOUNTID")
    @Column(name = "SOLO_MOBILE_ACCOUNTID")
    private String soloMobileAccountId;

    @JsonProperty("USER_NAME")
    @Column(name = "USER_NAME")
    private String userName;

    @JsonProperty("CUSTOMER_ROLE")
    @Column(name = "CUSTOMER_ROLE")
    private String customerRole;

    @JsonProperty("CUSTOMER_FIRST_NAME")
    @Column(name = "CUSTOMER_FIRST_NAME")
    private String customerFirstName;

    @JsonProperty("CUSTOMER_LAST_NAME")
    @Column(name = "CUSTOMER_LAST_NAME")
    private String customerLastName;

    @JsonProperty("PHONE_NUMBER")
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @JsonProperty("EMAIL_ADDRESS")
    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;

    @JsonProperty("CREATE_USER_ID")
    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @JsonProperty("CREATE_TS")
    @Column(name = "CREATE_TS")
    private String createTs;

    @JsonProperty("LAST_UPDATE_USER")
    @Column(name = "LAST_UPDATE_USER")
    private String lastUpdateUser;

    @JsonProperty("LAST_UPDATE_TS")
    @Column(name = "LAST_UPDATE_TS")
    private String lastUpdateTs;

    @JsonProperty("ADDRESS_LINE1")
    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @JsonProperty("ADDRESS_LINE2")
    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @JsonProperty("CITY")
    @Column(name = "CITY")
    private String city;

    @JsonProperty("STATE")
    @Column(name = "STATE")
    private String state;

    @JsonProperty("ZIP_CODE5")
    @Column(name = "ZIP_CODE5")
    private String zipCode5;

    @JsonProperty("ZIP_CODE4")
    @Column(name = "ZIP_CODE4")
    private String zipCode4;

    @JsonProperty("BUSINESS_NAME")
    @Column(name = "BUSINESS_NAME")
    private String businessName;

    @JsonProperty("USER_TYPE")
    @Column(name = "USER_TYPE")
    private String userType;

    @JsonProperty("ACTIVITY_CODE")
    @Column(name = "ACTIVITY_CODE")
    private String activityCode;

    @JsonProperty("IDENTIFICATION_TYPE")
    @Column(name = "IDENTIFICATION_TYPE")
    private String identificationType;

    @JsonProperty("TAX_GEO_CODE")
    @Column(name = "TAX_GEO_CODE")
    private String taxGeoCode;

    @JsonProperty("COMMENTS")
    @Column(name = "COMMENTS")
    private String comments;

    @JsonProperty("PREFERRED_NAME")
    @Column(name = "PREFERRED_NAME")
    private String preferredName;

    @JsonProperty("PRONOUN")
    @Column(name = "PRONOUN")
    private String pronoun;

    @JsonProperty("ACCOUNT_ID_NUM")
    @Column(name = "ACCOUNT_ID_NUM")
    private Long accountIdNum;
}