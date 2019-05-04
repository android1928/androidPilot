package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.utils.ServerResponse;

public class AddCouponResultEvent extends BaseResultEvent {
    public AddCouponResultEvent(){
        super(ServerResponse.REQUEST_TIMEOUT);
    }
    public AddCouponResultEvent(Object... args) {
        super(args);
    }
}
