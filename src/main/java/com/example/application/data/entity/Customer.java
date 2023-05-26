package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;

@Entity
public class Customer extends AbstractEntity {

    private Integer customerId;
    private String customerFirm;
    private String customerStreet;
    private Integer customerHouseNr;
    private String customerZIP;
    private String customerCity;
    private String customerCountry;
    private String customerFirmPhone;
    @Email
    private String customerFirmMail;
    private String customerContactPerson;
    private String customerContactPhone;
    @Email
    private String customerContactMail;

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public String getCustomerFirm() {
        return customerFirm;
    }
    public void setCustomerFirm(String customerFirm) {
        this.customerFirm = customerFirm;
    }
    public String getCustomerStreet() {
        return customerStreet;
    }
    public void setCustomerStreet(String customerStreet) {
        this.customerStreet = customerStreet;
    }
    public Integer getCustomerHouseNr() {
        return customerHouseNr;
    }
    public void setCustomerHouseNr(Integer customerHouseNr) {
        this.customerHouseNr = customerHouseNr;
    }
    public String getCustomerZIP() {
        return customerZIP;
    }
    public void setCustomerZIP(String customerZIP) {
        this.customerZIP = customerZIP;
    }
    public String getCustomerCity() {
        return customerCity;
    }
    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }
    public String getCustomerCountry() {
        return customerCountry;
    }
    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }
    public String getCustomerFirmPhone() {
        return customerFirmPhone;
    }
    public void setCustomerFirmPhone(String customerFirmPhone) {
        this.customerFirmPhone = customerFirmPhone;
    }
    public String getCustomerFirmMail() {
        return customerFirmMail;
    }
    public void setCustomerFirmMail(String customerFirmMail) {
        this.customerFirmMail = customerFirmMail;
    }
    public String getCustomerContactPerson() {
        return customerContactPerson;
    }
    public void setCustomerContactPerson(String customerContactPerson) {
        this.customerContactPerson = customerContactPerson;
    }
    public String getCustomerContactPhone() {
        return customerContactPhone;
    }
    public void setCustomerContactPhone(String customerContactPhone) {
        this.customerContactPhone = customerContactPhone;
    }
    public String getCustomerContactMail() {
        return customerContactMail;
    }
    public void setCustomerContactMail(String customerContactMail) {
        this.customerContactMail = customerContactMail;
    }

}
