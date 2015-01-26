package ru.startandroid.vkclient.friends;

import android.app.Activity;
import android.os.Bundle;

import ru.startandroid.vkclient.R;

/**
 * @author Samofal Vitaliy
 * Тестовое активити. TODO
 */
public class FriendsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        getFragmentManager().beginTransaction().add(R.id.friends_container,new FriendsFragment()).commit();
    }
}
