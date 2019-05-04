package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.utils.ServerResponse;

public class ApplyCouponResultEvent extends BaseResultEvent {
    public double finalPrice;
    public ApplyCouponResultEvent(){
        super(ServerResponse.REQUEST_TIMEOUT);
    }
    public ApplyCouponResultEvent(Object... args) {
        super(args);
        if(hasError())
            return;
        this.finalPrice = (double)args[1];
    }
}
