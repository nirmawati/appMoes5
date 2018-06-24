package com.example.nirma.moes5;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/*
    this class handled fragment in main activity
 */
class TabsPagerAdapter extends FragmentPagerAdapter
{
    public TabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    //list of fragment
    @Override
    public Fragment getItem(int position) {
        switch (position) {
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

    //many of fragment
    @Override
    public int getCount() {
        return 3 ;
    }

    //current position in fragment
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Teman";
            case 2:
                return "Just For You";
            default:
                return null;

        }
    }
}
