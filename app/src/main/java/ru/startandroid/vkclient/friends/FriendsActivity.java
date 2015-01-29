package ru.startandroid.vkclient.friends;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.fragments.FriendsFragment;

/**
 * @author Samofal Vitaliy
 * Тестовое активити. TODO
 */
public class FriendsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        getSupportFragmentManager().beginTransaction().add(R.id.friends_container,new FriendsFragment()).commit();
    }
}
