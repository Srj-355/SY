package com.example.sy.dto;

public class BusDTO {
    private String busName;
    private String busNumber;
    private String busType;
    private String operatorName;
    private String departureTime;
    private String departureStation;
    private String arrivalTime;
    private String arrivalStation;
    private Double fare;
    private Boolean isActive;

    // Constructors, Getters and Setters
    public BusDTO() {}

    public BusDTO(String busName, String busNumber, String busType, String operatorName, 
                 String departureTime, String departureStation, String arrivalTime, 
                 String arrivalStation, Double fare, Boolean isActive) {
        this.busName = busName;
        this.busNumber = busNumber;
        this.busType = busType;
        this.operatorName = operatorName;
        this.departureTime = departureTime;
        this.departureStation = departureStation;
        this.arrivalTime = arrivalTime;
        this.arrivalStation = arrivalStation;
        this.fare = fare;
        this.isActive = isActive;
    }

    // Getters and Setters for all fields
    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }
    public Double getFare() { return fare; }
    public void setFare(Double fare) { this.fare = fare; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}