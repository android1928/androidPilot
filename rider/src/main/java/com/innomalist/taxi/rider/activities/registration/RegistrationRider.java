package com.innomalist.taxi.rider.activities.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.activities.about.AboutActivity;

import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RegistrationRider extends AppCompatActivity implements View.OnClickListener {

    private Button sendNumBt, sendActivCodeBt;
    private EditText numEd, activCodeEd;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseAuthSettings firebaseAuthSettings;
    private PhoneAuthProvider phoneAuthProvider;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String numberStr;
    private Intent mainMenuIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_rider);
        findElements();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mainMenuIntent = new Intent(RegistrationRider.this, AboutActivity.class);
        selectVisible(sendNumBt, numEd, sendActivCodeBt, activCodeEd);

        try {
            authStateListener = firebaseAuth -> {
                if (user != null) {

                } else {
                    Toast.makeText(this, "please authorize in app", Toast.LENGTH_SHORT).show();
                }
            };
        } catch (Exception e) {
            Toast.makeText(this, "Undefinded error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        int regRandomInt = (int) (Math.random() * 999);
        String regRandomIntStr = String.valueOf(regRandomInt);
        auth.createUserWithEmailAndPassword(numberStr + regRandomIntStr + "@gmail.com", "android1928").addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Toast.makeText(this,"Registration error",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Hello!",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendNumberBt:
                selectVisible(sendActivCodeBt, activCodeEd, sendNumBt, numEd);
                numberStr = phoneNumberStr();
                sendVerificationCode(numberStr);
                break;

            case R.id.sendActivationCodeBt:
                verifyVerificationCode(verificationCodeStr());
                break;

            default:
                break;
        }
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, this, callbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                activCodeEd.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegistrationRider.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            resendToken = forceResendingToken;
        }
    };

    private void verifyVerificationCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(RegistrationRider.this, task -> {
                    if (task.isSuccessful()) {
                        mainMenuIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
/*
                        createAccount();
*/
                        startActivity(mainMenuIntent);
                    } else {
                        String message = "Undefinded Error on Sign In";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code!";
                        }
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Dismiss", v -> {
                        });
                        snackbar.show();
                    }
                });
    }


    private String verificationCodeStr() {
        String code = activCodeEd.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            activCodeEd.setError("Enter valid code!");
            activCodeEd.requestFocus();
        }
        return code;
    }

    private String phoneNumberStr() {
        String number = numEd.getText().toString().trim();
        if (number.isEmpty()) {
            numEd.setError("Enter a valid number!");
            numEd.requestFocus();
        }
        return number;
    }

    private void findElements() {
        sendNumBt = findViewById(R.id.sendNumberBt);
        sendNumBt.setOnClickListener(this);

        sendActivCodeBt = findViewById(R.id.sendActivationCodeBt);
        sendActivCodeBt.setOnClickListener(this);

        numEd = findViewById(R.id.regPhoneNumber);
        activCodeEd = findViewById(R.id.regActivateCode);
    }

    private void selectVisible(Button visBt, EditText visEd, Button invisBt, EditText invisEd) {
        visBt.setVisibility(View.VISIBLE);
        visEd.setVisibility(View.VISIBLE);
        invisBt.setVisibility(View.GONE);
        invisEd.setVisibility(View.GONE);
    }
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

}
