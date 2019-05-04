package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseResultEvent;

public class ServiceFinishedEvent extends BaseResultEvent {
    public boolean isCreditUsed;
    public float amount;
    public ServiceFinishedEvent(int code, boolean isCreditUsed, float amount){
        super(code);
        this.isCreditUsed = isCreditUsed;
        this.amount = amount;
    }
}
