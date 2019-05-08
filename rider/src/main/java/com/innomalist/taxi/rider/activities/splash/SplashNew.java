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
import com.innomalist.taxi.rider.activities.about.AboutActivity;
import com.innomalist.taxi.rider.activities.registration.RegistrationRider;

public class SplashNew extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    private Intent mainMenuIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_new);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mainMenuIntent = new Intent(this, AboutActivity.class);

        Button loginButton = findViewById(R.id.login_button_new);
        Button signOutBt = findViewById(R.id.signOutBt);

        signOutBt.setOnClickListener(v ->{
            auth.signOut();
        });

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        authStateListener = firebaseAuth -> {
            if (user != null) {
                loginButton.setOnClickListener(v -> {

                    try {

                user = auth.getCurrentUser();
                startActivity(mainMenuIntent);
                //TODO вход на главный экран приложения
                Toast.makeText(this, "You are authorized!! "+user.getEmail() , Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Undefinded error!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                loginButton.setOnClickListener(v -> {

                    try {
                Intent regIntent = new Intent(this, RegistrationRider.class);
                startActivity(regIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Undefinded error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
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
