package ru.startandroid.vkclient;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pc on 26.01.2015.
 */
public class FragmentListMessages extends ListFragment implements FragmentMessagesListener {

    final String MESSAGES = "Сообщения";

    MainActivityMessagesListener mainActivityMessagesListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().sendBroadcast(new Intent(MainActivity.AFTER_FRAGMENT_MESSAGES_ADDED_ACTION));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivityMessagesListener = (MainActivityMessagesListener) activity;
    }

    @Override
    public void setAdapter() {
        ArrayList<Map<String,Object>> arrayList = new ArrayList<Map<String,Object>>();
        for(int i=0; i<50; i++){
            Map<String, Object> m = new HashMap<String, Object>();
            m.put(MESSAGES,MESSAGES);
            arrayList.add(i,m);
        }
        String[] from = {MESSAGES};
        int[] to = {R.id.tw_messages};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),arrayList,R.layout.item_fragment_messages,from,to);
        setListAdapter(simpleAdapter);
    }
}
