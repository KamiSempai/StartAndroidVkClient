package ru.startandroid.vkclient.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.LinkedList;
import ru.startandroid.vkclient.gcm.LongPoolService;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.ChatAdapter;
import ru.startandroid.vkclient.ChatMessage;
import ru.startandroid.vkclient.ChatRequest;

/**
 * Фрагмент с чатом
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    BroadcastReceiver mBroadcastReceiver;
    ChatAdapter mChatAdapter;
    LinkedList<ChatMessage> mMessageArray;
    ListView mListView;
    Button mButton;
    EditText mEditText;
    String mUserId,mMessage;
    final int UNREAD = 1;
    final int OUTBOX = 2;
    final String CHAT_SIZE = "50";

    public ChatFragment(String userId){
        mUserId = userId;
        mMessageArray = new LinkedList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new BroadcastReceiver() {
            // Ловим broadcast от LongPoolService с сообщением, добавляем к списку и обновляем ListView
            @Override
            public void onReceive(Context context, Intent intent) {
               if (intent.getAction().equals(LongPoolService.NEW_MESSAGE_SERVICE_ACTION)
                        && intent.getStringExtra(LongPoolService.NEW_MESSAGE_USER_ID_KEY).equals(mUserId)){
               mMessageArray.add(new ChatMessage()
                           .setReadState((intent.getIntExtra(LongPoolService.NEW_MESSAGE_FLAG_KEY,0)&UNREAD)!=UNREAD)// Проверка флага - прочитанное/не прочитанное
                           .setOut((intent.getIntExtra(LongPoolService.NEW_MESSAGE_FLAG_KEY,0)&OUTBOX)==OUTBOX)// Проверка флага - входящее/исходящее
                           .setId(Integer.valueOf(intent.getStringExtra(LongPoolService.NEW_MESSAGE_MESSAGE_ID)))// id
                           .setBody(intent.getStringExtra(LongPoolService.NEW_MESSAGE_TEXT_KEY)));// текст
                mChatAdapter.notifyDataSetChanged();
                mListView.smoothScrollToPosition(100500);

               }


            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LongPoolService.NEW_MESSAGE_SERVICE_ACTION);
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, null);
        mListView = (ListView) view.findViewById(R.id.lw_chat);
        mListView.setStackFromBottom(true);
        mButton = (Button) view.findViewById(R.id.bt_chat);
        mButton.setOnClickListener(this);
        mEditText = (EditText) view.findViewById(R.id.et_chat);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Создаем адаптер, ставим его и отравляем запрос на историю сообщений
        mChatAdapter = new ChatAdapter(getActivity(),mMessageArray,R.layout.item_chat,R.id.tw_item_chat);
        new ChatRequest(mUserId,CHAT_SIZE,mMessageArray,mChatAdapter).execute();
        mListView.setAdapter(mChatAdapter);
    }

    @Override
    public void onClick(View v) {
        // Отправить
        mMessage = mEditText.getText().toString();
        VKRequest vkRequest = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,mUserId,VKApiConst.MESSAGE,mMessage));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                // Тут пока ничего не пишем, сигнал о том, что сообщение отправлено, придет из LongPoolService в onReceive
                // Там сообщение и добавим к списку
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Toast.makeText(getActivity(),"Сообщение не отправлено",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Toast.makeText(getActivity(),"Сообщение не отправлено",Toast.LENGTH_SHORT).show();
            }
        });
        mEditText.setText("");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
