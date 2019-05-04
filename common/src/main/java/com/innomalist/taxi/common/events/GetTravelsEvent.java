package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class GetTravelsEvent extends BaseRequestEvent {
    public GetTravelsEvent() {
        super(new GetTravelsResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue(),null));
    }
}
