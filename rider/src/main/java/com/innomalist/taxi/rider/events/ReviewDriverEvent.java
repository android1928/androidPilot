package com.innomalist.taxi.rider.events;

import com.innomalist.taxi.common.events.BaseRequestEvent;
import com.innomalist.taxi.common.models.Review;
import com.innomalist.taxi.common.utils.ServerResponse;

public class ReviewDriverEvent extends BaseRequestEvent {
    public Review review;
    public ReviewDriverEvent(Review review){
        super(new ReviewDriverResultEvent(ServerResponse.REQUEST_TIMEOUT.getValue()));
        this.review = review;
    }
}
