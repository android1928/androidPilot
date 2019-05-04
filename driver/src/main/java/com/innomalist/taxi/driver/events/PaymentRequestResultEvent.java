package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.events.BaseResultEvent;

public class PaymentRequestResultEvent extends BaseResultEvent {
    public PaymentRequestResultEvent(int code) {
        super(code);
    }
}
