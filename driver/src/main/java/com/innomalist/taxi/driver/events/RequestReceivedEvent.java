package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.models.Request;
import com.innomalist.taxi.common.models.TravelSerializable;

public class RequestReceivedEvent {
    public Request request;

    public RequestReceivedEvent(String travelJson,Integer travelDistance,Integer fromDriver, Double cost) {
        this.request = new Request(TravelSerializable.fromJson(travelJson),travelDistance,fromDriver,cost);
    }
}