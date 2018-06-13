package com.example.nirma.moes5;

import android.app.DownloadManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.firebase.FirebaseTooManyRequestsException;

class TabsPagerAdapter extends FragmentPagerAdapter
{
    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 2:
                JFYFragment jfyFragment = new JFYFragment();
                return jfyFragment;

             default:
                 return null;
        }
    }

    @Override
    public int getCount()
    {
        return 3 ;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Chats";
            case 1:
                return "Friends";
            case 2:
                return "JFY";
            default:
                return null;

        }
    }
}
