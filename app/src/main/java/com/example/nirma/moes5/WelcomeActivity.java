package com.example.nirma.moes5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/*
    This class is creating a splash screen
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(3000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();

                }
                finally
                {
                    //move to start activity
                    Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                }

            }
        };
        thread.start();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
