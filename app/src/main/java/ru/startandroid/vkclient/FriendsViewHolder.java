package ru.startandroid.vkclient;

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

    private ImageView mAvatar = null;
    private TextView mFirstLastNames = null;
    private TextView mOnline = null;

    public FriendsViewHolder(View view){
        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        mFirstLastNames = (TextView) view.findViewById(R.id.first_last_name);
        mOnline = (TextView) view.findViewById(R.id.online);
    }

    public ImageView getAvatar() {
        return mAvatar;
    }

    public TextView getFirstLastNames() {
        return mFirstLastNames;
    }

    public TextView getOnline() {
        return mOnline;
    }
}
