package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class ServiceCallRequestEvent extends BaseRequestEvent {
    public ServiceCallRequestEvent() {
        super(new ServiceCallRequestResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
    }
}
