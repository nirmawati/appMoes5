package com.example.nirma.moes5;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private Button LoginButton;
    private EditText LoginEmail;
    private EditText LoginPass;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mToolbar =  findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoginButton = (Button) findViewById(R.id.login_button);
        LoginEmail = (EditText) findViewById(R.id.login_email);
        LoginPass = (EditText) findViewById(R.id.login_pass);
        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = LoginEmail.getText().toString();
                String pass = LoginPass.getText().toString();

                LoginUserAccount(email,pass);
            }
        });
    }

    private void LoginUserAccount(String email, String pass)
    {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,"Please write your email !",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(LoginActivity.this,"Please write your password !",Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please Wait, while we are verifying your credentials...");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Wrong Email and Password,Please wride your valid email and password !", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }
}
