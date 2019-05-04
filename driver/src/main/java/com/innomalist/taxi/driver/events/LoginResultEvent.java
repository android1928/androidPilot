package com.innomalist.taxi.driver.events;

import com.google.gson.Gson;
import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.models.Driver;

public class LoginResultEvent extends BaseResultEvent {
    public Driver driver;
    public String driverJson;
    public String jwtToken;
    public LoginResultEvent(int response, String driverJson, String jwtToken) {
        super(response);
        this.driverJson = driverJson;
        this.driver = new Gson().fromJson(driverJson,Driver.class);
        this.jwtToken = jwtToken;
    }
    public LoginResultEvent(Object... args) {
        super(args);
    }
}
