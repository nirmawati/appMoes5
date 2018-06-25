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

import com.example.nirma.moes5.model.Penduduk;
import com.example.nirma.moes5.network.Network;
import com.example.nirma.moes5.network.Routes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    this class create new account
 */
public class SignupActivity extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;

    //API Connect
    private Routes network;

    //UI
    private EditText SignUpUserNik;
    private EditText SignUpUserEmail;
    private EditText SignUpUserPass;
    private Button CreateAccountButton;

    //Progress Bar
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //create layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        network = Network.request().create(Routes.class);//connect to api
        mAuth = FirebaseAuth.getInstance();//connect to firebase

        //Set UI
        mToolbar = findViewById(R.id.signup_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get UI Component
        SignUpUserNik =  findViewById(R.id.signup_nik);
        SignUpUserEmail =  findViewById(R.id.signup_email);
        SignUpUserPass =  findViewById(R.id.signup_pass);
        CreateAccountButton =  findViewById(R.id.create_button);
        loadingBar = new ProgressDialog(this);

        //Create Account
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String nik = SignUpUserNik.getText().toString();
                isNikCorrect(nik);
            }
        });
    }

    private void isNikCorrect(final String nik) {
        network.getPenduduk(nik).enqueue(new Callback<List<Penduduk>>() {
            @Override
            public void onResponse(Call<List<Penduduk>> call, Response<List<Penduduk>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        Penduduk penduduk = response.body().get(0);
                        //Cek this nik gender
                        if (penduduk.getGender().equals("P")) {
                            //get data
                            String name = penduduk.getName();//from api
                            //from text box
                            String nik = SignUpUserNik.getText().toString();
                            String email = SignUpUserEmail.getText().toString();
                            String pass = SignUpUserPass.getText().toString();

                            SignUpAccount(name, nik, email, pass);
                        } else {
                            Toast.makeText(getApplicationContext(), "Maaf, anda tidak bisa masuk kedalam aplikasi", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Maaf, NIK anda tidak terdaftar", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Penduduk>> call, Throwable t) {

            }
        });
    }

    private void SignUpAccount(final String name, final String nik, String email, String pass)
    {
        //cek is empty in text box
        if (TextUtils.isEmpty(nik))
        {
            Toast.makeText(SignupActivity.this, "Maaf, NIK belum dimasukkan", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(SignupActivity.this, "Maaf, Email belum dimasukkan", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(pass))
        {
            Toast.makeText(SignupActivity.this, "Maaf, Password belum dimasukkan", Toast.LENGTH_LONG).show();
        } else
            {
                //set info in loading bar
                loadingBar.setTitle("Membuat akun baru");
                loadingBar.setMessage("Mohon tunggu, sedang dalam proses pembuatan akun");
                loadingBar.show();

                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            //get user id in firebase
                            String current_user_id = mAuth.getCurrentUser().getUid();

                            //set id to firebase
                            storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                            //set data to firebase
                            storeUserDefaultDataReference.child("user_name").setValue(name);
                            storeUserDefaultDataReference.child("user_nik").setValue(nik);
                            storeUserDefaultDataReference.child("user_status").setValue("Saya baru masuk");
                            storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                            storeUserDefaultDataReference.child("device_token").setValue(deviceToken);
                            storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                    //after compleate insert data to firebase
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                //move to MainActivity
                                                Intent mainIntent = new Intent(SignupActivity.this,MainActivity.class);
                                                //set flag
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            Toast.makeText(SignupActivity.this, "Maaf, Pendaftaran gagal", Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                });
            }
    }
}
