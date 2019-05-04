package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.models.Travel;

public class SendTravelInfoEvent {
    public Travel travel;
    public SendTravelInfoEvent(Travel travel) {
        this.travel = travel;
    }
}
