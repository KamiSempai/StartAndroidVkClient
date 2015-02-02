package ru.startandroid.vkclient.activities;


import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import ru.startandroid.vkclient.gcm.GCM;
import ru.startandroid.vkclient.gcm.LongPollConnection;
import ru.startandroid.vkclient.MainActivityMessagesListener;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.fragments.ChatFragment;
import ru.startandroid.vkclient.fragments.FragmentListMessages;
import ru.startandroid.vkclient.fragments.FriendsFragment;


public class MainActivity extends ActionBarActivity implements MainActivityMessagesListener,ActionBar.OnNavigationListener {

    // Broacast сервису LongPoolService на уничтожение при закрытии MainActivity
    public static final String DESTROY_SERVICE_ACTION = "ru.startandroid.vkclient.DESTROY_SERVICE";
    public static final String LOG_TAG = "myLogs";
    String[] mSpinnerNames = new String[] { "Сообщения", "Друзья" };
    GCM mGcm;
    private FragmentListMessages mFragmentListMessages;
    private FriendsFragment mFriendsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        setContentView(R.layout.activity_main);
        this.setTitle("");
        mFriendsFragment = new FriendsFragment();
        mFragmentListMessages = new FragmentListMessages();
        setFragmentListMessages();
        setActionBarSpinner();
        mGcm = new GCM(this);
        new LongPollConnection(this).connect();// Запуск LongPoolService

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
        sendBroadcast(new Intent().setAction(DESTROY_SERVICE_ACTION));
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
        // Ставим фрагмент с чатом(времмено)
        setChatFragment(id);
    }

    private void setFragmentListFriends(){

        if (mFriendsFragment.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFriendsFragment)
                .commit();
    }

    private void setFragmentListMessages(){
        if (mFragmentListMessages.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFragmentListMessages)
                .commit();
    }

    private void setChatFragment(String id){
        ChatFragment chatFragment = new ChatFragment(id);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, chatFragment)
                .commit();
    }

    private void setActionBarSpinner(){
        //Установка выпадающего списка в ActionBar
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_spinner_item, mSpinnerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setListNavigationCallbacks(adapter, this);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        switch (i){
            case 0:
                setFragmentListMessages();
                break;
            case 1:
                setFragmentListFriends();
                break;
        }
        return false;
    }


}
