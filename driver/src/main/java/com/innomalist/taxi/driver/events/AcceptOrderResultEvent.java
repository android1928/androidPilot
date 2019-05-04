package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.models.Travel;

public class AcceptOrderResultEvent extends BaseResultEvent {
    public Travel travel;
    public AcceptOrderResultEvent(Object... args) {
        super(args);
        travel = Travel.fromJson(args[0].toString());
    }
}
