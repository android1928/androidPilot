package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class ConnectEvent extends BaseRequestEvent {
    public String token;
    public ConnectEvent(String token){
        super(new ConnectResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
        this.token = token;
    }
}
