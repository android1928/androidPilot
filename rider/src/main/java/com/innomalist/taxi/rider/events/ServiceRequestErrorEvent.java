package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;

public class ServiceRequestErrorEvent extends BaseResultEvent {
    public ServiceRequestErrorEvent(int response,String message) {
        super(response,message);
    }
    public ServiceRequestErrorEvent(int response) {
        super(response);
    }
}
