package ru.startandroid.vkclient.friends;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.startandroid.vkclient.R;

/**
 * @author Samofal Vitaliy
 */
// Создан для использования в адаптере. Для того что - бы метод getView()
//  не вызывал findViewById, множество раз (Прямопропорционально количеству элементов списка).
public class FriendsViewHolder {

    private ImageView avatar = null;
    private TextView firstLastNames = null;
    private TextView online = null;

    public FriendsViewHolder(View view){
        avatar = (ImageView) view.findViewById(R.id.avatar);
        firstLastNames = (TextView) view.findViewById(R.id.first_last_name);
        online = (TextView) view.findViewById(R.id.online);
    }

    public ImageView getAvatar() {
        return avatar;
    }

    public TextView getFirstLastNames() {
        return firstLastNames;
    }

    public TextView getOnline() {
        return online;
    }
}
