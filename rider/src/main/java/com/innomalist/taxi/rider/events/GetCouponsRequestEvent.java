package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;

public class GetCouponsRequestEvent extends BaseRequestEvent {
    public GetCouponsRequestEvent() {
        super(new GetCouponsResultEvent());
    }
}
