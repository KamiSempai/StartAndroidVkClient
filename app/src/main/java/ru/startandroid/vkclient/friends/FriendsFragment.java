package ru.startandroid.vkclient.friends;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.startandroid.vkclient.R;

/**
 * @author Samofal Vitaliy
 * Фрагмент отвечающий за сбор информации и отображение друзей.
 */

public class FriendsFragment extends ListFragment {
    private FriendsArray friendsArray = new FriendsArray();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FriendsAdapter friendsAdapter = new FriendsAdapter(getActivity(),R.layout.friends_list,R.id.first_last_name,friendsArray.getFriends());
        FriendsRequest friendsRequest = new FriendsRequest(GeneralFriendsFields.FIRSTNAME_LASTNAME_ICON_ONLINE,friendsAdapter,friendsArray);
        setListAdapter(friendsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment,container,false);
    }
}
