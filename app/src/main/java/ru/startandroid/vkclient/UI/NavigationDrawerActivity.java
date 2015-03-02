package ru.startandroid.vkclient.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vk.sdk.VKSdk;

import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.gcm.GCM;

/**
 * Created by pc on 01.03.2015.
 */
public abstract class NavigationDrawerActivity extends ActionBarActivity {


    protected DrawerLayout mDrawerLayout;
    protected Toolbar mToolbar;
    protected GCM mGcm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGcm = new GCM(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        String[] mDrawerArray = getResources().getStringArray(R.array.drawer_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.nd_open,R.string.nd_close ));
        ListView mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerArray));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        onClickChooseChat();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        onClickFriends();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:
                        onClickLogout();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 4:
                        mGcm.registerDevice();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 5:
                        mGcm.unRegisterDevice();
                        mDrawerLayout.closeDrawers();
                        break;
                }
            }
        });
    }

    public abstract void onClickFriends();
    public abstract void onClickChooseChat();
    public abstract void onClickLogout();

}
