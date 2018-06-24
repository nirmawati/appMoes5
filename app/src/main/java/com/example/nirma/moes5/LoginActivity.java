package com.example.nirma.moes5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/*
    this class handled login user
 */
public class LoginActivity extends AppCompatActivity
{
    //Firebase
    private FirebaseAuth mAuth;

    //UI
    private Button LoginButton;
    private EditText LoginEmail;
    private EditText LoginPass;
    private ProgressDialog loadingBar;
    private Toolbar mToolbar;

    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Create view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set firebase
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //set UI
        mToolbar =  findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Masuk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get UI from layout
        LoginButton = findViewById(R.id.login_button);
        LoginEmail = findViewById(R.id.login_email);
        LoginPass = findViewById(R.id.login_pass);
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
        //cek isempty
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this, "Maaf, Email belum dimasukkan", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(LoginActivity.this, "Maaf, Password belum dimasukkan", Toast.LENGTH_LONG).show();
        }
        else
        {
            //set UI
            loadingBar.setTitle("Masuk Akun");
            loadingBar.setMessage("Mohon tunggu sebentar. Akun anda sedang dalam proses.");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        String onlineUserId = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        usersReference.child(onlineUserId).child("device_token").setValue(deviceToken)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //move to main activity
                                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        //flag
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Maaf, Email atau Password yang anda masukkan salah", Toast.LENGTH_SHORT).show();
                    }
                    loadingBar.dismiss();
                }
            });
        }
    }
}
