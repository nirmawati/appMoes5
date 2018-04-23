package com.example.nirma.moes5;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText SignUpUserName;
    private EditText SignUpUserEmail;
    private EditText SignUpUserPass;
    private Button CreateAccountButton;

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SignUpUserName = (EditText) findViewById(R.id.signup_name);
        SignUpUserEmail = (EditText) findViewById(R.id.signup_email);
        SignUpUserPass = (EditText) findViewById(R.id.signup_pass);
        CreateAccountButton = (Button) findViewById(R.id.create_button);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = SignUpUserName.getText().toString();
                String email = SignUpUserEmail.getText().toString();
                String pass = SignUpUserPass.getText().toString();

                SignUpAccount (name, email, pass);
            }
        });
    }

    private void SignUpAccount(String name, String email, String pass)
    {
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(SignupActivity.this,"Please write your name !",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(SignupActivity.this,"Please write your email !",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(SignupActivity.this,"Please write your password !",Toast.LENGTH_LONG).show();
        } else
            {
                loadingBar.setTitle("Creating New Account");
                loadingBar.setMessage("Please wait, while we are creating account for you");
                loadingBar.show();

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }else {
                            Toast.makeText(SignupActivity.this, "Error Accured, Try Again...", Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                });
            }
    }

    public void setSupportActionBar(Toolbar mToolbar) {
    }
}
