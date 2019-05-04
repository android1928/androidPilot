package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;

public class GetRequestsRequestEvent extends BaseRequestEvent {
    public GetRequestsRequestEvent() {
        super(new GetRequestsResultEvent());
    }
}
