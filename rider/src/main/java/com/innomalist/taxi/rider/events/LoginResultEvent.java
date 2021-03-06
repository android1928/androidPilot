package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.models.Rider;
import com.innomalist.taxi.common.events.BaseResultEvent;

public class LoginResultEvent extends BaseResultEvent {
    public Rider rider;
    public String riderJson;
    public String jwtToken;
    public LoginResultEvent(int response, String riderJson, String jwtToken) {
        super(response);
        this.riderJson = riderJson;
        this.rider = new Rider().fromJson(riderJson);
        this.jwtToken = jwtToken;
    }
    public LoginResultEvent(int response, String message) {
        super(response,message);
    }
}
