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
import ru.startandroid.vkclient.fragments.ChooseChatFragment;
import ru.startandroid.vkclient.gcm.GCM;
import ru.startandroid.vkclient.gcm.LongPollConnection;
import ru.startandroid.vkclient.MainActivityMessagesListener;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.fragments.ChatFragment;
import ru.startandroid.vkclient.fragments.FriendsFragment;
import ru.startandroid.vkclient.gcm.LongPollService;


public class MainActivity extends ActionBarActivity implements MainActivityMessagesListener,ActionBar.OnNavigationListener {

    public static final String LOG_TAG = "myLogs";
    final String CURRENT_FRAGMENT_KEY = "CURRENT_FRAGMENT_KEY";
    final String CURRENT_USER_ID_KEY = "CURRENT_USER_ID_KEY";
    final int CHOOSE_CHAT_FRAGMENT = 0;
    final int FRIENDS_FRAGMENT = 1;
    final int CHAT_FRAGMENT = 2;
    int mCurrentFragment;
    String[] mSpinnerNames;
    GCM mGcm;
    private ChooseChatFragment mChooseChatFragment;
    private FriendsFragment mFriendsFragment;
    private String mUserId;
    private boolean restoreState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinnerNames = new String[] { getString(R.string.messages), getString(R.string.friends) };
        VKUIHelper.onCreate(this);
        this.setTitle("");
        mFriendsFragment = new FriendsFragment();
        mChooseChatFragment = new ChooseChatFragment();
        if(savedInstanceState == null){
            setFragmentListMessages();
        }else if(savedInstanceState!=null && savedInstanceState.containsKey(CURRENT_FRAGMENT_KEY)){
            if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == CHOOSE_CHAT_FRAGMENT){
                setFragmentListMessages();
            }else if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == FRIENDS_FRAGMENT){
                setFragmentListFriends();
            }else if(savedInstanceState.getInt(CURRENT_FRAGMENT_KEY) == CHAT_FRAGMENT
                    && savedInstanceState.containsKey(CURRENT_USER_ID_KEY)){
                setChatFragment(savedInstanceState.getString(CURRENT_USER_ID_KEY));
            }

        }
        setActionBarSpinner();
        mGcm = new GCM(this);
        new LongPollConnection(this).connect();// Запуск LongPollService

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreState = true;
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

    private void setFragmentListFriends(){
        if (mFriendsFragment.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFriendsFragment)
                .commit();
        mCurrentFragment = FRIENDS_FRAGMENT;
    }

    private void setFragmentListMessages(){
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
        if (i == 0 && !restoreState){
            setFragmentListMessages();
        }else if (i == 1 && !restoreState){
            setFragmentListFriends();
        }
        restoreState = false;
        return false;
    }


}
