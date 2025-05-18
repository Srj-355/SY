package com.example.sy.model;

import jakarta.persistence.*;


@Entity
@Table(name = "buses")
public class Bus {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String busName;

    @Column( unique = true)
    private String busNumber;

    @Column
    private String busType;

    @Column
    private String operatorName;

    @Column
    private String departureTime;

    @Column
    private String departureStation;

    @Column
    private String arrivalTime;

    @Column
    private String arrivalStation;

    @Column
    private Double fare;

    @Column
    private Boolean isActive = true;

    public Bus() {
    }

    public Bus(String busName,String busNumber, String busType, String operatorName,
               String departureTime,String departureStation, String arrivalTime,String arrivalStation, Double fare) {
        this.busName = busName;
        this.busNumber = busNumber;
        this.busType = busType;
        this.operatorName = operatorName;
        this.departureTime = departureTime;
        this.departureStation = departureStation;
        this.arrivalTime = arrivalTime;
        this.arrivalStation = arrivalStation;
        this.fare = fare;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getBusName() { return busName; }

    public void setBusName(String busName) { this.busName = busName; }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}