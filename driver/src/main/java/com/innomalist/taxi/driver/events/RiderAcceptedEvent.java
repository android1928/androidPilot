package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.models.Travel;

public class RiderAcceptedEvent {
    public Travel travel;

    public RiderAcceptedEvent(Object... args) {
        this.travel = Travel.fromJson(args[0].toString());
    }
}
