package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;

public class GetPromotionsRequestEvent extends BaseRequestEvent {
    public GetPromotionsRequestEvent() {
        super(new GetPromotionsResultEvent());
    }
}
