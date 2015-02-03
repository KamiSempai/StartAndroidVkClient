package ru.startandroid.vkclient.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import ru.startandroid.vkclient.fragments.ChooseChatFragment;
import ru.startandroid.vkclient.gcm.GCM;
import ru.startandroid.vkclient.gcm.LongPollConnection;
import ru.startandroid.vkclient.MainActivityMessagesListener;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.fragments.ChatFragment;
import ru.startandroid.vkclient.fragments.FriendsFragment;
import ru.startandroid.vkclient.gcm.LongPollService;


public class MainActivity extends ActionBarActivity implements MainActivityMessagesListener {

    public static final String LOG_TAG = "myLogs";
    final String CURRENT_FRAGMENT_KEY = "CURRENT_FRAGMENT_KEY";
    final String CURRENT_USER_ID_KEY = "CURRENT_USER_ID_KEY";
    final int CHOOSE_CHAT_FRAGMENT = 0;
    final int FRIENDS_FRAGMENT = 1;
    final int CHAT_FRAGMENT = 2;
    int mCurrentFragment;
    GCM mGcm;
    private ChooseChatFragment mChooseChatFragment;
    private FriendsFragment mFriendsFragment;
    private String mUserId;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VKUIHelper.onCreate(this);
        this.setTitle("");
        mFriendsFragment = new FriendsFragment();
        mChooseChatFragment = new ChooseChatFragment();
        if(savedInstanceState == null){
            setChooseChatFragment();
        }else if(savedInstanceState!=null && savedInstanceState.containsKey(CURRENT_FRAGMENT_KEY)){
            if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == CHOOSE_CHAT_FRAGMENT){
                setChooseChatFragment();
            }else if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == FRIENDS_FRAGMENT){
                setFriendsFragment();
            }else if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == CHAT_FRAGMENT
                    && savedInstanceState.containsKey(CURRENT_USER_ID_KEY)){
                setChatFragment(savedInstanceState.getString(CURRENT_USER_ID_KEY));
            }

        }
        mGcm = new GCM(this);
        new LongPollConnection(this).connect();// Запуск LongPollService
        // Toolbar и Navigation Drawer
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
                        setChooseChatFragment();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        setFriendsFragment();
                        mDrawerLayout.closeDrawers();
                        break;
                }
            }
        });
  }


    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putInt(CURRENT_FRAGMENT_KEY,mCurrentFragment);
        saveInstanceState.putString(CURRENT_USER_ID_KEY,mUserId);
    }

    private void logout(){
        VKSdk.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
        sendBroadcast(new Intent().setAction(LongPollService.DESTROY_SERVICE_ACTION));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }else if (id == R.id.action_gcm_on) {
            mGcm.registerDevice();
            return true;
        }else if (id == R.id.action_gcm_off) {
            mGcm.unRegisterDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void eventFromFragmentListMessages(String id) {
        // Ставим фрагмент с чатом(временно)
        setChatFragment(id);
    }

    private void setFriendsFragment(){
        if (mFriendsFragment.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFriendsFragment)
                .commit();
        mCurrentFragment = FRIENDS_FRAGMENT;
    }

    private void setChooseChatFragment(){
        if (mChooseChatFragment.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mChooseChatFragment)
                .commit();
        mCurrentFragment = CHOOSE_CHAT_FRAGMENT;
    }

    private void setChatFragment(String id){
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserId(id);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, chatFragment)
                .commit();
        mCurrentFragment = CHAT_FRAGMENT;
        mUserId = id;
    }




}
