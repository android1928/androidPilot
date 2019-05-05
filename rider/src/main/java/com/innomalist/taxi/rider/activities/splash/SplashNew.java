package com.innomalist.taxi.rider.activities.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.activities.registration.RegistrationRider;

public class SplashNew extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_new);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        Button loginButton = findViewById(R.id.login_button_new);
        loginButton.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(v -> {
            Intent regIntent = new Intent(this, RegistrationRider.class);
            startActivity(regIntent);
        });

        auth = FirebaseAuth.getInstance();
        try{
        authStateListener = firebaseAuth -> {
            if(user!=null){
                //TODO вход на главный экран приложения
            }else {
                loginButton.setVisibility(View.VISIBLE);
            }
        };
        }catch (Exception e){
            Toast.makeText(this,"Undefinded error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(authStateListener!=null){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
