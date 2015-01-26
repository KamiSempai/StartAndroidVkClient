package ru.startandroid.vkclient.friends;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.startandroid.vkclient.R;

/**
 * @author Samofal Vitaliy
 * Класс адаптер для построения списка друзей
 */
public class FriendsAdapter extends ArrayAdapter<FriendBuilder> {

    public FriendsAdapter(Context context, int resource, int textViewResourceId, List<FriendBuilder> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
        FriendBuilder currentFriend = getItem(position);
        FriendsViewHolder friendsViewHolder = (FriendsViewHolder) view.getTag();
        if(friendsViewHolder == null){
            friendsViewHolder = new FriendsViewHolder(view);
            view.setTag(friendsViewHolder);
        }
        friendsViewHolder.getAvatar().setImageResource(R.drawable.ic_launcher);
        friendsViewHolder.getFirstLastNames().setText(currentFriend.getFirstName() + " " + currentFriend.getLastName());
        friendsViewHolder.getOnline().setText(currentFriend.getOnline() ? "ON" : "OFF");
        friendsViewHolder.getOnline().setTextColor(currentFriend.getOnline() ? Color.GREEN : Color.RED);
        return view;
    }
}
