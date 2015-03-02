package ru.startandroid.vkclient.UI;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;


import ru.startandroid.vkclient.gcm.GCM;
import ru.startandroid.vkclient.gcm.LongPollConnection;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.gcm.LongPollService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class MainActivity extends NavigationDrawerActivity implements ChooseChatFragment.ChooseChatFragmentListener,ChatFragment.ChatFragmentListener {

    public static final String LOG_TAG = "myLogs";
    final String CURRENT_FRAGMENT_KEY = "CURRENT_FRAGMENT_KEY";
    final String CURRENT_USER_ID_KEY = "CURRENT_USER_ID_KEY";
    final int CHOOSE_CHAT_FRAGMENT = 0;
    final int FRIENDS_FRAGMENT = 1;
    final int CHAT_FRAGMENT = 2;
    int mCurrentFragment;
    private ChooseChatFragment mChooseChatFragment;
    private FriendsFragment mFriendsFragment;
    private String mUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!isConnect(getBaseContext())) {
            Toast.makeText(this, "Нет подключения к сети", Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
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
        LongPollConnection.connect(this);// Запуск LongPollService

  }

    @Override
    public void onClickFriends() {
        setFriendsFragment();
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onClickChooseChat() {
        setChooseChatFragment();
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void logout() {
        VKSdk.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private boolean isConnect(Context c) {

        final ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(c.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = connMgr.getActiveNetworkInfo();
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ( nInfo != null && nInfo.isConnected())
            return true;

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putInt(CURRENT_FRAGMENT_KEY,mCurrentFragment);
        saveInstanceState.putString(CURRENT_USER_ID_KEY,mUserId);
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
    public void eventFromFragmentListMessages(String id) {
        // Ставим фрагмент с чатом(временно)
        setChatFragment(id);
    }

    public void setFriendsFragment(){
        if (mFriendsFragment.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mFriendsFragment)
                .commit();
        mCurrentFragment = FRIENDS_FRAGMENT;
    }

    public void setChooseChatFragment(){
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


    @Override
    public void onChooseFriendsFragment() {
        setFriendsFragment();
    }

    @Override
    public void onChooseChooseChatFragment() {
        setChooseChatFragment();
    }

    @Override
    public void onLogout() {
        logout();
    }
}
