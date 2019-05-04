package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class GetStatusEvent extends BaseRequestEvent {
    public GetStatusEvent() {
        super(new GetStatusResultEvent(ServerResponse.REQUEST_TIMEOUT));
    }
}
