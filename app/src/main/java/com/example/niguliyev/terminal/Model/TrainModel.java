package com.example.niguliyev.terminal.Model;

import org.json.JSONObject;

public class TrainModel {

    private int trainId;
    private  String tripDate;
    private  String trainNumber;

    public TrainModel(int trainId, String tripDate, String trainNumber){
        this.tripDate = tripDate;
        this.trainId = trainId;
        this.trainNumber = trainNumber;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }
}
