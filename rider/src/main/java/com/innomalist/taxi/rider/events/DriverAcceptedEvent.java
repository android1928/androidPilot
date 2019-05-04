package com.innomalist.taxi.rider.events;

import com.google.gson.Gson;
import com.innomalist.taxi.common.models.Driver;
import com.innomalist.taxi.common.models.DriverInfo;

public class DriverAcceptedEvent {
    public Driver driver;
    public DriverAcceptedEvent(Object... args) {
        Gson gson = new Gson();
        driver = gson.fromJson(args[0].toString(), Driver.class);
    }
}
