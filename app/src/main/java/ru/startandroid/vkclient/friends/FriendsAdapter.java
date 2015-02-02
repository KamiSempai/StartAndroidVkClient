package ru.startandroid.vkclient.friends;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vk.sdk.api.model.VKApiUserFull;

import java.util.List;

import ru.startandroid.vkclient.R;

/**
 * @author Samofal Vitaliy
 * Класс адаптер для построения списка друзей
 */
public class FriendsAdapter extends ArrayAdapter<VKApiUserFull> {

    private int mCountOfAllUsers;
    private boolean mIsUpdatedData = true;

    public FriendsAdapter(Context context, int resource, int textViewResourceId, List<VKApiUserFull> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position,convertView,parent);
        VKApiUserFull currentFriend = getItem(position);
        FriendsViewHolder friendsViewHolder = (FriendsViewHolder) view.getTag();
        if(friendsViewHolder == null){
            friendsViewHolder = new FriendsViewHolder(view);
            view.setTag(friendsViewHolder);
        }
        friendsViewHolder.getAvatar().setImageResource(R.drawable.ic_launcher);
        friendsViewHolder.getFirstLastNames().setText(currentFriend.first_name + " " + currentFriend.last_name);
        friendsViewHolder.getOnline().setText(currentFriend.online ? "ON" : "OFF");
        friendsViewHolder.getOnline().setTextColor(currentFriend.online ? Color.GREEN : Color.RED);
        return view;
    }

    public int getCountOfAllUsers() {
        return mCountOfAllUsers;
    }


    public void setCountOfAllUsers(int mCountOfAllUsers) {
        this.mCountOfAllUsers = mCountOfAllUsers;
    }

    public boolean isUpdatedData() {
        return mIsUpdatedData;
    }

    public void setIsUpdatedData(boolean mIsUpdatedData) {
        this.mIsUpdatedData = mIsUpdatedData;
    }
}
