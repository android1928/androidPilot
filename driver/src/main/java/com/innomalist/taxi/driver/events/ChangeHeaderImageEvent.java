package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;
import com.innomalist.taxi.common.utils.ServerResponse;

public class ChangeHeaderImageEvent extends BaseRequestEvent {
    public String path;
    public ChangeHeaderImageEvent(String path){
        super(new ChangeHeaderImageResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue(),null));
        this.path = path;
    }
}
