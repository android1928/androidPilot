package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;

public class AddCouponRequestEvent extends BaseRequestEvent {
    public String code;
    public AddCouponRequestEvent(String code) {
        super(new AddCouponResultEvent());
        this.code = code;
    }
}
