package com.innomalist.taxi.common.events;

import com.innomalist.taxi.common.utils.ServerResponse;

public class EditProfileInfoEvent extends BaseRequestEvent {
    public String userInfo;
    public EditProfileInfoEvent(String userInfo){
        super(new EditProfileInfoResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
        this.userInfo = userInfo;
    }
}
