package com.innomalist.taxi.driver.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;
import com.innomalist.taxi.common.utils.ServerResponse;

public class ChangeStatusEvent extends BaseRequestEvent {
    public ChangeStatusEvent(Status status) {
        super(new ChangeStatusResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
        this.status = status;
    }
    public enum Status {
        ONLINE("online"),
        OFFLINE("offline");
        private String value;

        Status(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    public Status status;
}
