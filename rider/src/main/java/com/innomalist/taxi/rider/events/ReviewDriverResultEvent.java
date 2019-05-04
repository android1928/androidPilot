package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;

public class ReviewDriverResultEvent extends BaseResultEvent {
    public ReviewDriverResultEvent(int response) {
        super(response);
    }
}
