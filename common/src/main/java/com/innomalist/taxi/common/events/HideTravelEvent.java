package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class HideTravelEvent extends BaseRequestEvent {
    public Integer travelId;
    public HideTravelEvent(Integer travelId) {
        super(new HideTravelResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
        this.travelId = travelId;
    }
}
