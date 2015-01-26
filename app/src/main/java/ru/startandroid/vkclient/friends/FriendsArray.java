package ru.startandroid.vkclient.friends;

import java.util.ArrayList;
/**
 * @author Samofal Vitaliy
 */
public class FriendsArray {
    private ArrayList<FriendBuilder> friends = new ArrayList<>();

    public void add(FriendBuilder friend) {
        friends.add(friend);
    }

    public ArrayList<FriendBuilder> getFriends() {
        return friends;
    }
}
