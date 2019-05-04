package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;

public class ServiceRequestResultEvent extends BaseResultEvent {
    public int driversSentTo;
    public ServiceRequestResultEvent(int driversSentTo) {
        super(200);
        this.driversSentTo = driversSentTo;
    }
}
