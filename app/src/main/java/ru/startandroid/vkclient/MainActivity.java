package ru.startandroid.vkclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;



public class MainActivity extends ActionBarActivity implements  MainActivityFriendsListener, MainActivityMessagesListener,ActionBar.OnNavigationListener {

    public static final String LOG_TAG = "myLogs";

    // Broacast сервису LongPoolService на уничтожение при закрытии MainActivity
    public static final String DESTROY_SERVICE_ACTION = "ru.startandroid.vkclient.DESTROY_SERVICE";

    //Broadcast на вход
    public static final String NEW_MESSAGE_SERVICE_ACTION = "ru.startandroid.vkclient.NEW_MESSAGE_SERVICE_ACTION";
    public static final String USER_ONLINE_SERVICE_ACTION = "ru.startandroid.vkclient.USER_ONLINE_SERVICE_ACTION";
    public static final String USER_OFFLINE_SERVICE_ACTION = "ru.startandroid.vkclient.USER_OFFLINE_SERVICE_ACTION";
    public static final String USER_WRITES_SERVICE_ACTION = "ru.startandroid.vkclient.USER_WRITES_SERVICE_ACTION";
    public static final String AFTER_FRAGMENT_FRIENDS_ADDED_ACTION = "ru.startandroid.vkclient.AFTER_FRAGMENT_FRIENDS_ADDED_ACTION";
    public static final String AFTER_FRAGMENT_MESSAGES_ADDED_ACTION = "ru.startandroid.vkclient.AFTER_FRAGMENT_MESSAGES_ADDED_ACTION";

    //Ключи
    public static final String NEW_MESSAGE_USER_ID_KEY = "NEW_MESSAGE_USER_ID_KEY";
    public static final String NEW_MESSAGE_TEXT_KEY = "NEW_MESSAGE_TEXT_KEY";
    public static final String USER_ONLINE_USER_ID_KEY = "USER_ONLINE_USER_ID_KEY";
    public static final String USER_OFFLINE_USER_ID_KEY = "USER_OFFLINE_USER_ID_KEY";
    public static final String USER_WRITES_USER_ID_KEY = "USER_OFFLINE_USER_ID_KEY";

    String[] spinnerNames = new String[] { "Сообщения", "Друзья" };

    BroadcastReceiver broadcastReceiver;
    GCM gsm;
    FragmentListFriends fragmentListFriends;
    FragmentListMessages fragmentListMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        setContentView(R.layout.activity_main);
        this.setTitle("");
        fragmentListFriends = new FragmentListFriends();
        fragmentListMessages = new FragmentListMessages();
        setFragmentListMessages(); // Ставим фрагмент со списком сообщений на старте активности(позже надо будет добавить сохранение при повороте экрана)
        setActionBarSpinner();
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(NEW_MESSAGE_SERVICE_ACTION)){
                    // Пришло новое сообщение от пользователя(id пользователя и текст)
                    Log.d(LOG_TAG,intent.getStringExtra(NEW_MESSAGE_TEXT_KEY)+
                            " от пользователя "+
                            intent.getStringExtra(NEW_MESSAGE_USER_ID_KEY));
                }else if(intent.getAction().equals(MainActivity.USER_WRITES_SERVICE_ACTION)){
                    // Пользователь набирает сообщение (id пользователя)
                    Log.d(LOG_TAG,"Пользователь "+
                            intent.getStringExtra(USER_WRITES_USER_ID_KEY)+
                            " пишет");
                }else if(intent.getAction().equals(MainActivity.USER_ONLINE_SERVICE_ACTION)){
                    // Друг стал онлайн (id друга)
                    Log.d(LOG_TAG,"Друг "+
                            intent.getStringExtra(USER_ONLINE_USER_ID_KEY)+
                            " онлайн");
                }else if(intent.getAction().equals(MainActivity.USER_OFFLINE_SERVICE_ACTION)){
                    // Друг ушел в оффлайн
                    Log.d(LOG_TAG,"Друг "+
                            intent.getStringExtra(USER_OFFLINE_USER_ID_KEY)+
                            " оффлайн");
                }else if(intent.getAction().equals(MainActivity.AFTER_FRAGMENT_FRIENDS_ADDED_ACTION)){
                    // Пришел Broadcast из onActivityCreated фрагмента - ставим адаптер
                    setAdapterForFragmentListFriends();
                }else if(intent.getAction().equals(MainActivity.AFTER_FRAGMENT_MESSAGES_ADDED_ACTION)){
                    // Пришел Broadcast из onActivityCreated фрагмента - ставим адаптер
                    setAdapterForFragmentListMessages();

                }
            }
        };
        IntentFilter intFilt = new IntentFilter();
        intFilt.addAction(NEW_MESSAGE_SERVICE_ACTION);
        intFilt.addAction(USER_WRITES_SERVICE_ACTION);
        intFilt.addAction(USER_ONLINE_SERVICE_ACTION);
        intFilt.addAction(USER_OFFLINE_SERVICE_ACTION);
        intFilt.addAction(AFTER_FRAGMENT_FRIENDS_ADDED_ACTION);
        intFilt.addAction(AFTER_FRAGMENT_MESSAGES_ADDED_ACTION);
        registerReceiver(broadcastReceiver, intFilt);

        gsm = new MyGCM(this);
        new LongPoolConnection(this).connect();// Запуск LongPoolService

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
            gsm.registerDevice();
            return true;
        }else if (id == R.id.action_gcm_off) {
            gsm.unRegisterDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void eventFromFragmentListFriends() {
        // Сигнал из фрагмента - делаем что то
    }

    @Override
    public void eventFromFragmentListMessages() {
        // Сигнал из фрагмента - делаем что то
    }

    private void setAdapterForFragmentListFriends(){
        // Пришел Broadcast из onActivityCreated фрагмента - ставим адаптер
        if(fragmentListFriends.isAdded()){
            FragmentFriendsListener fragmentFriendsListener = (FragmentFriendsListener) fragmentListFriends;
            fragmentFriendsListener.setAdapter();
        }
    }

    private void setAdapterForFragmentListMessages(){
        // Пришел Broadcast из onActivityCreated фрагмента - ставим адаптер
        if(fragmentListMessages.isAdded()){
            FragmentMessagesListener fragmentMessagesListener = (FragmentMessagesListener) fragmentListMessages;
            fragmentMessagesListener.setAdapter();
        }
    }

    private void setFragmentListFriends(){
        if (fragmentListFriends.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentListFriends)
                .commit();

    }

    private void setFragmentListMessages(){
        if (fragmentListMessages.isAdded())
            return;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentListMessages)
                .commit();
    }

    private void setActionBarSpinner(){
        //Установка выпадающего списка в ActionBar
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_spinner_item, spinnerNames);
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
