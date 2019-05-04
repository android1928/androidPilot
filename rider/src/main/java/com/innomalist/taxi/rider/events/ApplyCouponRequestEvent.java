package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;

public class ApplyCouponRequestEvent extends BaseRequestEvent {
    public String code;
    public ApplyCouponRequestEvent(String code) {
        super(new ApplyCouponResultEvent());
        this.code = code;
    }
}
