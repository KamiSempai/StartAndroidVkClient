package ru.startandroid.vkclient;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Адаптер для ListView ChatFragment
 */
public class ChatAdapter extends BaseAdapter {

    LinkedList<ChatMessage> mChatMessageArrayList;
    LayoutInflater mInflater;
    int mLayout;
    int mTextViewId;


    public ChatAdapter(Context context, LinkedList<ChatMessage> chatMessageArrayList, int layout, int textViewId){
        mChatMessageArrayList = chatMessageArrayList;
        mLayout = layout;
        mTextViewId = textViewId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mChatMessageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatMessageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = mInflater.inflate(mLayout,parent,false);
        }
        TextView messageTextView = (TextView) view.findViewById(mTextViewId);
        LinearLayout messageLayout = (LinearLayout) view.findViewById(R.id.messageLayout);
        ChatMessage message = mChatMessageArrayList.get(position);
        messageTextView.setText(message.getBody());// Установка текста сообщения
        messageLayout.setGravity(message.isOut() ? Gravity.RIGHT : Gravity.LEFT);// Установка gravity - входящие слева, исходящие справа
        messageTextView.setBackgroundColor(message.isOut() ? Color.parseColor("#BFEFFF") : Color.parseColor("#E8E8E8"));// Установка цвета - входящие серые, исходящие голубые
        messageLayout.setBackgroundColor(message.isRead() ? Color.parseColor("#FFFFFF") : Color.parseColor("#DCDCDC"));// Установка фона - непрочитанные затемненные

        return view;
    }

}