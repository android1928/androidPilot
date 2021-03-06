package com.innomalist.taxi.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Locale;

public class Request implements Serializable {
    public TravelSerializable travel;
    public Double distance;
    public Double fromDriver;
    public Double cost;

    public Request(TravelSerializable travel, Integer distance, Integer fromDriver, Double cost){
        this.travel = travel;
        this.distance = distance.doubleValue() / 1000;
        this.fromDriver = fromDriver.doubleValue() / 1000;
        this.cost = cost;
    }
    public Request(TravelSerializable travel, Double distance, Double fromDriver, Double cost){
        this.travel = travel;
        this.distance = distance;
        this.fromDriver = fromDriver;
        this.cost = cost;
    }
}
