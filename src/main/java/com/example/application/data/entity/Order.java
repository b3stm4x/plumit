package com.example.application.data.entity;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Order extends AbstractEntity {

    private Integer orderId;
    private Integer customerId;
    private String objectDescription;
    private String pickupAdress;
    private String pickupCountry;
    private String deliveryAdress;
    private String deliveryCountry;
    private Integer vehicleId;
    private LocalDate etd;
    private LocalDate eta;

    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public String getObjectDescription() {
        return objectDescription;
    }
    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }
    public String getPickupAdress() {
        return pickupAdress;
    }
    public void setPickupAdress(String pickupAdress) {
        this.pickupAdress = pickupAdress;
    }
    public String getPickupCountry() {
        return pickupCountry;
    }
    public void setPickupCountry(String pickupCountry) {
        this.pickupCountry = pickupCountry;
    }
    public String getDeliveryAdress() {
        return deliveryAdress;
    }
    public void setDeliveryAdress(String deliveryAdress) {
        this.deliveryAdress = deliveryAdress;
    }
    public String getDeliveryCountry() {
        return deliveryCountry;
    }
    public void setDeliveryCountry(String deliveryCountry) {
        this.deliveryCountry = deliveryCountry;
    }
    public Integer getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
    public LocalDate getEtd() {
        return etd;
    }
    public void setEtd(LocalDate etd) {
        this.etd = etd;
    }
    public LocalDate getEta() {
        return eta;
    }
    public void setEta(LocalDate eta) {
        this.eta = eta;
    }

}
