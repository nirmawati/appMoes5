package com.example.nirma.moes5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/*
    this class is for sign up and login
 */
public class StartPageActivity extends AppCompatActivity {

    //UI
    private Button SignupButton;
    private Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        //Get UI Component
        SignupButton =  findViewById(R.id.signup_button);
        LoginButton =  findViewById(R.id.login_button);

        //when click signup button, do this
        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //move to activity signup
                Intent signupIntent = new Intent(StartPageActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });

        //when click login button, do this
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //move to activity login
                Intent loginIntent = new Intent(StartPageActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
