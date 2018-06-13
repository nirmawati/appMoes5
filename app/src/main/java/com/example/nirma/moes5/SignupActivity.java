package com.example.nirma.moes5;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nirma.moes5.model.Penduduk;
import com.example.nirma.moes5.network.Network;
import com.example.nirma.moes5.network.Routes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;

    private EditText SignUpUserNik;
    private EditText SignUpUserEmail;
    private EditText SignUpUserPass;
    private Button CreateAccountButton;

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private Routes network;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        network = Network.request().create(Routes.class);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SignUpUserNik =  findViewById(R.id.signup_nik);
        SignUpUserEmail =  findViewById(R.id.signup_email);
        SignUpUserPass =  findViewById(R.id.signup_pass);
        CreateAccountButton =  findViewById(R.id.create_button);
        loadingBar = new ProgressDialog(this);

        SignUpUserNik.setText("7324035");
        SignUpUserEmail.setText("a@asfa.com");
        SignUpUserPass.setText("12345678");

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String nik = SignUpUserNik.getText().toString();
//                isNikCorrect(nik);
                test();
            }
        });
    }

    private void test() {
        network.getPenduduk().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void isNikCorrect(String nik) {
        network.getPenduduk(nik).enqueue(new Callback<List<Penduduk>>() {
            @Override
            public void onResponse(Call<List<Penduduk>> call, Response<List<Penduduk>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        Penduduk penduduk = response.body().get(0);
                        if (penduduk.getGender().equals("P")) {
                            String nik = SignUpUserNik.getText().toString();
                            String email = SignUpUserEmail.getText().toString();
                            String pass = SignUpUserPass.getText().toString();

                            SignUpAccount (nik, email, pass);
                        } else {
                            Toast.makeText(getApplicationContext(), "maaf, anda bukan perempuan", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "maaf, nik anda tidak terdaftar", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Penduduk>> call, Throwable t) {

            }
        });
    }

    private void SignUpAccount(final String name, String email, String pass)
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
                            String current_user_id = mAuth.getCurrentUser().getUid();
                            storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                            storeUserDefaultDataReference.child("user_name").setValue(name);
                            storeUserDefaultDataReference.child("user_status").setValue("Hey I am using Moes5 app");
                            storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                            storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "Error Accured, Try Again...", Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                });
            }
    }
}
