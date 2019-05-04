package com.innomalist.taxi.common.activities.chargeAccount;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.innomalist.taxi.common.R;
import com.innomalist.taxi.common.components.BaseActivity;
import com.innomalist.taxi.common.events.ChargeAccountEvent;

public class PaymentCallbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_callback);
        eventBus.post(new ChargeAccountEvent("online","token",100));
    }
}
