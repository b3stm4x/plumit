package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Vehicle extends AbstractEntity {

    private Integer vehicleId;
    private String vehicleName;
    private String vehicleType;
    private Integer vehicleMaxWeiht;
    private Integer vehicleMaxLength;
    private Integer vehicleMaxWidth;
    private Integer vehicleMaxHeight;
    private String vehicleComment;

    public Integer getVehicleId() {
        return vehicleId;
    }
    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }
    public String getVehicleName() {
        return vehicleName;
    }
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public Integer getVehicleMaxWeiht() {
        return vehicleMaxWeiht;
    }
    public void setVehicleMaxWeiht(Integer vehicleMaxWeiht) {
        this.vehicleMaxWeiht = vehicleMaxWeiht;
    }
    public Integer getVehicleMaxLength() {
        return vehicleMaxLength;
    }
    public void setVehicleMaxLength(Integer vehicleMaxLength) {
        this.vehicleMaxLength = vehicleMaxLength;
    }
    public Integer getVehicleMaxWidth() {
        return vehicleMaxWidth;
    }
    public void setVehicleMaxWidth(Integer vehicleMaxWidth) {
        this.vehicleMaxWidth = vehicleMaxWidth;
    }
    public Integer getVehicleMaxHeight() {
        return vehicleMaxHeight;
    }
    public void setVehicleMaxHeight(Integer vehicleMaxHeight) {
        this.vehicleMaxHeight = vehicleMaxHeight;
    }
    public String getVehicleComment() {
        return vehicleComment;
    }
    public void setVehicleComment(String vehicleComment) {
        this.vehicleComment = vehicleComment;
    }

}
