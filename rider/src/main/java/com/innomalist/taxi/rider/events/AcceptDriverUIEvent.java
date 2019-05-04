package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.models.DriverInfo;

public class AcceptDriverUIEvent {
    public DriverInfo driverInfo;
    public AcceptDriverUIEvent(DriverInfo driverInfo){
        this.driverInfo = driverInfo;
    }
}
