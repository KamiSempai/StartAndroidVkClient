package ru.startandroid.vkclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 26.01.2015.
 */
public class FragmentListFriends extends ListFragment implements FragmentFriendsListener {

    final String FRIENDS = "Друзья";

    MainActivityFriendsListener mainActivityFriendsListener;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().sendBroadcast(new Intent(MainActivity.AFTER_FRAGMENT_FRIENDS_ADDED_ACTION));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityFriendsListener = (MainActivityFriendsListener) activity;
    }

    @Override
    public void setAdapter() {
        ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        for(int i=0; i<50; i++){
            Map<String, Object> m = new HashMap<String, Object>();
            m.put(FRIENDS,FRIENDS);
            arrayList.add(i,m);
        }
        String[] from = {FRIENDS};
        int[] to = {R.id.tw_friends};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),arrayList,R.layout.item_fragment_friends,from,to);
        setListAdapter(simpleAdapter);
    }
}
