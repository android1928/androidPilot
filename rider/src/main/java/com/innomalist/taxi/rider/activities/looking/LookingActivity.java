package com.innomalist.taxi.rider.activities.looking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.innomalist.taxi.common.components.BaseActivity;
import com.innomalist.taxi.common.models.Travel;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.databinding.ActivityLookingBinding;
import com.innomalist.taxi.rider.events.DriverAcceptedEvent;
import com.innomalist.taxi.rider.events.CancelRequestRequestEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.databinding.DataBindingUtil;

public class LookingActivity extends BaseActivity {
    ActivityLookingBinding binding;
    Travel travel = new Travel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersive(true);
        binding = DataBindingUtil.setContentView(LookingActivity.this, R.layout.activity_looking);
        travel = Travel.fromJson(getIntent().getStringExtra("travel"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDriverAccepted(DriverAcceptedEvent event) {
        binding.loadingIndicator.pauseAnimation();
        Intent intent = new Intent();
        intent.putExtra("driver", event.driver.toJson());
        this.setResult(RESULT_OK,intent);
        this.finish();

    }

    public void onCancelRequest(View view) {
        eventBus.post(new CancelRequestRequestEvent());
        this.setResult(RESULT_CANCELED);
        this.finish();
    }
}
