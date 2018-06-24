package com.example.nirma.moes5;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;

    //UI
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //create Layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set firebase
        mAuth = FirebaseAuth.getInstance();

        //tabs untuk main actifity
        myViewPager =  findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout =  findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        mToolbar =  findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Moes5");

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //get my user account
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if not user
        if (currentUser == null)
        {
            //log out
            LogOutUser();
        }
    }
    private void LogOutUser()
    {
        //go to start activity
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu,menu);
         return true;
    }

    //create menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //log out menu
        if (item.getItemId()== R.id.main_logout_button) {
            //log out from user
            mAuth.signOut();
            //change activity
            LogOutUser();
        }
        //setting menu
        if(item.getItemId() == R.id.main_settings_button) {
            //go to setting activity
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        //friend menu
        if(item.getItemId() == R.id.main_all_users_button) {
            //go to alluser activity
            Intent allUserIntent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUserIntent);
        }
        return true;
    }
}
