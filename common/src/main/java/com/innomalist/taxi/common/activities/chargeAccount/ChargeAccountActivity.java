package com.innomalist.taxi.common.activities.chargeAccount;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import cz.msebera.android.httpclient.Header;
import money.paybox.payboxsdk.Interfaces.PBListener;
import money.paybox.payboxsdk.Model.Capture;
import money.paybox.payboxsdk.Model.Card;
import money.paybox.payboxsdk.Model.Error;
import money.paybox.payboxsdk.Model.PStatus;
import money.paybox.payboxsdk.Model.RecurringPaid;
import money.paybox.payboxsdk.Model.Response;
import money.paybox.payboxsdk.PBHelper;
import money.paybox.payboxsdk.Utils.Constants;
import ru.cloudpayments.sdk.cp_card.CPCard;
import ru.cloudpayments.sdk.three_ds.ThreeDSDialogListener;
import ru.cloudpayments.sdk.three_ds.ThreeDsDialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.innomalist.taxi.common.CloudPaymentsApi;
import com.innomalist.taxi.common.R;
import com.innomalist.taxi.common.components.BaseActivity;
import com.innomalist.taxi.common.databinding.ActivityChargeAccountBinding;
import com.innomalist.taxi.common.events.ChargeAccountEvent;
import com.innomalist.taxi.common.events.ChargeAccountResultEvent;
import com.innomalist.taxi.common.utils.AlerterHelper;
import com.innomalist.taxi.common.utils.CommonUtils;
import com.innomalist.taxi.common.utils.NumberThousandWatcher;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.savvyapps.togglebuttonlayout.Toggle;
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Token;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class ChargeAccountActivity extends BaseActivity implements PBListener, ThreeDSDialogListener {
    ActivityChargeAccountBinding binding;
    final int GET_NEW_CARD = 2;
    final int PAY_BOX_PAYMENT = 102;
    final int CLOUD_PAYMENT = 103;

    //String clientToken = "";
    //private static final int REQUEST_CODE = 243;
    private enum PaymentMode {
        paybox,
        cloudPayments,
        online
    }
    PaymentMode paymentMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PBHelper.Builder builder = new PBHelper.Builder(getApplicationContext(),
                getResources().getString(R.string.paybox_getting_payment_key), Integer.parseInt(getResources().getString(R.string.paybox_merchant_id)));
        builder.setPaymentSystem(Constants.PBPAYMENT_SYSTEM.EPAYWEBKZT);
        builder.setPaymentCurrency(Constants.CURRENCY.KZT);
        builder.enabledTestMode(true);
        builder.setPaymentLifeTime(300);
        builder.build();

        PBHelper.getSdk().registerPbListener(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_charge_account);
        initializeToolbar(getString(R.string.title_charge));
        binding.editText.setText(String.valueOf(Math.round(getIntent().getDoubleExtra("defaultAmount", 0f))));
        binding.paymentToggleLayout.setOnToggleSelectedListener((toggle, selected) -> {
            if (!selected) {
                paymentMode = null;
                binding.checkoutButton.setEnabled(false);
                binding.checkoutButton.setText(getString(R.string.checkout_empty));
                return;
            }
            if (toggle.getId() == R.id.cloud_payments) {
                binding.checkoutButton.setText(getString(R.string.checkout_filled, getString(R.string.checkout_cloud_payments)));
                paymentMode = PaymentMode.cloudPayments;
                binding.checkoutButton.setEnabled(true);
            } else if (toggle.getId() == R.id.paybox) {
                binding.checkoutButton.setText(getString(R.string.checkout_filled, getString(R.string.checkout_paybox)));
                paymentMode = PaymentMode.paybox;
                binding.checkoutButton.setEnabled(true);
            }
        });
        if (!getResources().getBoolean(R.bool.payment_stripe_enabled) || !getResources().getBoolean(R.bool.payment_braintree_enabled)) {
            binding.paymentToggleLayout.setVisibility(View.GONE);
            binding.titleMethod.setVisibility(View.GONE);
            if (getResources().getBoolean(R.bool.payment_stripe_enabled)) {
                binding.checkoutButton.setText(getString(R.string.checkout_filled, getString(R.string.checkout_paybox)));
                paymentMode = PaymentMode.paybox;
                binding.checkoutButton.setEnabled(true);
            } else if (getResources().getBoolean(R.bool.payment_braintree_enabled)) {
                binding.checkoutButton.setText(getString(R.string.checkout_filled, getString(R.string.checkout_cloud_payments)));
                paymentMode = PaymentMode.cloudPayments;
                binding.checkoutButton.setEnabled(true);
            }
        }
        binding.editText.addTextChangedListener(new NumberThousandWatcher(binding.editText));

        Double balance = CommonUtils.driver != null ? CommonUtils.driver.getBalance() : CommonUtils.rider.getBalance();
        binding.textCurrentBalance.setText(getString(R.string.unit_money, balance));
        binding.chargeAddFirst.setText(getString(R.string.unit_money, getResources().getInteger(R.integer.charge_first) * 1d));
        binding.chargeAddSecond.setText(getString(R.string.unit_money, getResources().getInteger(R.integer.charge_second) * 1d));
        binding.chargeAddThird.setText(getString(R.string.unit_money, getResources().getInteger(R.integer.charge_third) * 1d));
        binding.chargeAddFirst.setOnClickListener(view -> addCharge(R.integer.charge_first));
        binding.chargeAddSecond.setOnClickListener(view -> addCharge(R.integer.charge_second));
        binding.chargeAddThird.setOnClickListener(view -> addCharge(R.integer.charge_third));
    }

    public static String randomKeyOrder()
    {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(10);
        for(int i=0;i<10;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void onCheckoutClicked(View view) {
        if(binding.editText.getText().toString().isEmpty()){
            AlerterHelper.showError(ChargeAccountActivity.this,getString(R.string.error_charge_field_empty));
            return;
        }
        int amount = (Integer.parseInt(binding.editText.getText().toString().replace(",","")));
        if(amount < getResources().getInteger(R.integer.minimum_charge_amount)) {
            AlerterHelper.showError(ChargeAccountActivity.this,getString(R.string.error_charge_field_low,getResources().getInteger(R.integer.minimum_charge_amount)));
            return;
        }
        switch (paymentMode) {
            case paybox:
                /*
                Intent intent = new Intent(ChargeAccountActivity.this, CardEditActivity.class);
                startActivityForResult(intent, GET_NEW_CARD);*/
                String orderKey = randomKeyOrder();
                //eventBus.post(new ChargeAccountEvent("paybox", "231231", amount));
                PBHelper.getSdk().initNewPayment(orderKey, "NULL_ID", amount, "Add to balance: " + amount + " Order ID: " + orderKey, null);
                break;

            case cloudPayments:
                Intent intent = new Intent(ChargeAccountActivity.this, CardEditActivity.class);
                startActivityForResult(intent, GET_NEW_CARD);

                /*
                if(!clientToken.isEmpty()) {
                    startBraintree();
                    return;
                }
                binding.checkoutButton.setEnabled(false);
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(getString(R.string.server_address) + "braintree_client_token", new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        binding.checkoutButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String clientToken) {
                        binding.checkoutButton.setEnabled(true);
                        ChargeAccountActivity.this.clientToken = clientToken;
                        startBraintree();
                    }
                });*/
                break;
        }
    }

    /*
    private void startBraintree() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(this.clientToken);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AccountCharged(ChargeAccountResultEvent event){
        if(event.hasError())
            event.showAlert(ChargeAccountActivity.this);
        else {
            setResult(RESULT_OK);
            finish();
        }
    }

    void addCharge(int resId) {
        try {
            binding.editText.setText(getString(resId));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Float amount = Float.parseFloat(binding.editText.getText().toString().replace(",",""));


        if(requestCode == GET_NEW_CARD && resultCode == RESULT_OK)
        {
            CPCard card = new CPCard(data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER), data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY),
                    data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV));

            String crypthogram = "";
            String errorMessage = "";

            try {
                crypthogram = card.cardCryptogram(getResources().getString(R.string.cardpaymnets_public_key));
            } catch (UnsupportedEncodingException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            } catch (BadPaddingException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }

            if (crypthogram.equals(""))
            {
                AlerterHelper.showError(ChargeAccountActivity.this, errorMessage);
                return;
            }

            String finalCrypthogram = crypthogram;

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        CloudPaymentsApi.ResultApi<String> res = new CloudPaymentsApi(getApplicationContext()).paymentToken(finalCrypthogram, String.valueOf(amount), CreditCardUtils.EXTRA_CARD_HOLDER_NAME);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.message.equals("3DS"))
                                {
                                    ThreeDsDialogFragment.newInstance(res.result.split("|||")[1],
                                            res.result.split("|||")[2],
                                            res.result.split("|||")[0])
                                            .show(getSupportFragmentManager(), "3DS");
                                }
                                else
                                {
                                    if (res.message.equals("TRUE"))
                                    {
                                        Float amount = Float.parseFloat(binding.editText.getText().toString().replace(",",""));

                                        eventBus.post(new ChargeAccountEvent("cloudpayments", res.result, amount));
                                    }
                                    else if (res.message.equals("FALSE"))
                                    {
                                        AlerterHelper.showError(ChargeAccountActivity.this, res.result);
                                    }
                                    else
                                    {
                                        AlerterHelper.showError(ChargeAccountActivity.this, res.message);
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            /*
            MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                    .title("Charging Wallet")
                    .content("Please wait...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            Card card = new Card(data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER),
                    Integer.valueOf(data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY).split("/")[0]),
                    Integer.valueOf(data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY).split("/")[1]),
                    data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV));
            Stripe stripe = new Stripe();
            stripe.createToken(card, getString(R.string.stripe_publishable_key), new TokenCallback() {
                public void onSuccess(Token token) {
                    eventBus.post(new ChargeAccountEvent("stripe", token.getId(),amount));
                }

                public void onError(Exception error) {
                    Log.e("Stripe", error.getLocalizedMessage());
                    materialDialog.dismiss();

                }
            });*/
        }
        /*
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                eventBus.post(new ChargeAccountEvent("braintree", result.getPaymentMethodNonce().getNonce(), amount));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                AlerterHelper.showError(ChargeAccountActivity.this, error.getMessage());
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PBHelper.getSdk().removePbListener(this);
    }

    @Override
    public void onCardList(ArrayList<Card> cards) {

    }

    @Override
    public void onAuthorizationCompleted(String md, String paRes)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    CloudPaymentsApi.ResultApi<String> res = new CloudPaymentsApi(getApplicationContext()).post3DS(md, paRes);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res.message.equals("TRUE"))
                            {
                                Float amount = Float.parseFloat(binding.editText.getText().toString().replace(",",""));

                                eventBus.post(new ChargeAccountEvent("cloudpayments", res.result, amount));
                            }
                            else if (res.message.equals("FALSE"))
                            {
                                AlerterHelper.showError(ChargeAccountActivity.this, res.result);
                            }
                            else
                            {
                                AlerterHelper.showError(ChargeAccountActivity.this, res.message);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAuthorizationFailed(String html)
    {
        AlerterHelper.showError(ChargeAccountActivity.this, html);
    }

    @Override
    public void onPaymentRevoke(Response response) {
        Log.e("PAYBOX_EROROR", response.getPaymentId());
    }

    @Override
    public void onPaymentPaid(Response response)
    {
        Log.e("PAYBOX_EROROR", response.getPaymentId());
        Float amount = Float.parseFloat(binding.editText.getText().toString().replace(",",""));

        eventBus.post(new ChargeAccountEvent("paybox", response.getPaymentId(), amount));
    }

    @Override
    public void onPaymentStatus(PStatus pStatus) {
        Log.e("PAYBOX_EROROR", pStatus.getCardPan());
    }

    @Override
    public void onCardAdded(Response response) {

    }

    @Override
    public void onCardRemoved(Card card) {
        Log.e("PAYBOX_EROROR", card.getCardhash());
    }

    @Override
    public void onCardPayInited(Response response) {
        Log.e("PAYBOX_EROROR", response.getPaymentId());
    }

    @Override
    public void onCardPaid(Response response) {
        Log.e("PAYBOX_EROROR", response.getPaymentId());
    }

    @Override
    public void onRecurringPaid(RecurringPaid recurringPaid) {
        Log.e("PAYBOX_EROROR", recurringPaid.getCurrency());
    }

    @Override
    public void onPaymentCaptured(Capture capture) {
        Log.e("PAYBOX_EROROR", capture.toString());
    }

    @Override
    public void onPaymentCanceled(Response response) {
        Log.e("PAYBOX_EROROR", response.getPaymentId());
    }

    @Override
    public void onError(Error error) {
        Log.e("PAYBOX_EROROR", error.getErrorDesription() + " " +error.getErrorCode());
    }
}
