package com.innomalist.taxi.driver.events;

import com.google.gson.Gson;
import com.innomalist.taxi.common.events.BaseResultEvent;
import com.innomalist.taxi.common.models.Media;

public class ChangeHeaderImageResultEvent extends BaseResultEvent {
    public Media media;
    public ChangeHeaderImageResultEvent(int code, String media) {
        super(code);
        this.media = new Gson().fromJson(media,Media.class);
    }
}
