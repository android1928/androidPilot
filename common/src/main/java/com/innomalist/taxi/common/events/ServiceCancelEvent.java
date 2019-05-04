package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class ServiceCancelEvent extends BaseRequestEvent {
    public ServiceCancelEvent(){
        super(new ServiceCallRequestResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));

    }
}
