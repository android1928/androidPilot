package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class ChangeProfileImageEvent extends BaseRequestEvent {
    public String path;
    public ChangeProfileImageEvent(String path){
        super(new ChangeProfileImageResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue(),null));
        this.path = path;
    }
}
