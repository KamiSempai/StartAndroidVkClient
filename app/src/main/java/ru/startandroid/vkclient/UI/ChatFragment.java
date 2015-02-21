package ru.startandroid.vkclient.UI;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.ListIterator;
import ru.startandroid.vkclient.gcm.LongPollService;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.ChatAdapter;
import ru.startandroid.vkclient.ChatMessage;
import ru.startandroid.vkclient.requests.ChatRequest;

/**
 * Фрагмент с чатом
 */
public class ChatFragment extends Fragment implements View.OnClickListener, ChatRequest.OnNewResponseListener {

    ChatRequest mChatRequest;
    ChatAdapter mChatAdapter;
    BroadcastReceiver mBroadcastReceiver;
    LinkedList<ChatMessage> mMessageArray;
    ListView mListView;
    EditText mEditText;
    TextView mTextViewUserWrites;
    ImageView mAttachmentImageView,mSendImageView;
    Dialog mDialog;
    String mUserId;
    public static final String CHAT_SIZE = "50";
    int totalMessageCount; // общее колличество сообщений в диалоге
    int totalDownloadedMessageCount; // колличество загруженных сообщений
    boolean onScrollState; // Будучи false не дает отправить новый запрос на новые сообщения(onScroll срабатывает несколько раз на одной позиции)


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalMessageCount = 0;
        onScrollState = false;
        mMessageArray = new LinkedList<>();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(LongPollService.NEW_MESSAGE_LP_ACTION) //Получение нового сообщения(LongPollService)
                        && intent.getStringExtra(LongPollService.NEW_MESSAGE_USER_ID_KEY).equals(mUserId)) {
                    onReceiveNewMessage(intent);
                } else if (intent.getAction().equals(LongPollService.USER_WRITES_LP_ACTION) // Пользователь набирает текст(LongPollService)
                        && intent.getStringExtra(LongPollService.USER_WRITES_USER_ID_KEY).equals(mUserId)) {
                    onReceiveUserWrites();
                } else if (intent.getAction().equals(LongPollService.USER_STOP_WRITES_LP_ACTION) // Пользователь набирает текст(LongPollService)
                        && intent.getStringExtra(LongPollService.USER_STOP_WRITES_USER_ID_KEY).equals(mUserId)) {
                    onReceiveUserStopWrites();
                }else if (intent.getAction().equals(LongPollService.READ_INPUT_MESSAGES_LP_ACTION) // Входящие сообщения прочитаны(LongPollService)
                        && intent.getStringExtra(LongPollService.USER_ID_INPUT_MESSAGE_KEY).equals(mUserId)) {
                    onReceiveInputRead(intent);
                } else if (intent.getAction().equals(LongPollService.READ_OUTPUT_MESSAGES_LP_ACTION) // Исходящие сообщения прочитаны(LongPollService)
                        && intent.getStringExtra(LongPollService.USER_ID_OUTPUT_MESSAGE_KEY).equals(mUserId) ) {
                    onReceiveOutputRead(intent);
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LongPollService.NEW_MESSAGE_LP_ACTION);
        intentFilter.addAction(LongPollService.USER_WRITES_LP_ACTION);
        intentFilter.addAction(LongPollService.USER_STOP_WRITES_LP_ACTION);
        intentFilter.addAction(LongPollService.READ_INPUT_MESSAGES_LP_ACTION);
        intentFilter.addAction(LongPollService.READ_OUTPUT_MESSAGES_LP_ACTION);
        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, null);
        mListView = (ListView) view.findViewById(R.id.lw_chat);
        mListView.setStackFromBottom(true);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Если ListView дошел до первого пункта, подгружаем еще 50 сообщений
                if (firstVisibleItem == 0 && onScrollState && totalDownloadedMessageCount < totalMessageCount){
                    onScrollState = false;
                    mChatRequest.downloadMessages(totalItemCount);
                }

            }
        });
        mSendImageView = (ImageView) view.findViewById(R.id.sendImageView);
        mSendImageView.setOnClickListener(this);
        mAttachmentImageView = (ImageView) view.findViewById(R.id.attachmentImageView);
        mAttachmentImageView.setOnClickListener(this);
        mEditText = (EditText) view.findViewById(R.id.et_chat);
        mTextViewUserWrites = (TextView) view.findViewById(R.id.tw_chat_text_writes);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // mUserId = getArguments().getString(FriendsFragment.id);
        // Создаем адаптер, ставим его и отравляем запрос на первые 50 сообщений
        mChatAdapter = new ChatAdapter(getActivity(),mMessageArray,R.layout.item_chat,2);
        mListView.setAdapter(mChatAdapter);
        mChatRequest = new ChatRequest(getActivity(),this,mUserId,CHAT_SIZE,mMessageArray,mChatAdapter);
        mChatRequest.downloadMessages(totalDownloadedMessageCount);

    }

    @Override
    public void onClick(View v) {
        // Обработка нажатий на imageView отправки сообщения и прикреплений.
        switch (v.getId()){
            case R.id.sendImageView:
                mChatRequest.sendMessage(mEditText.getText().toString());
                mEditText.setText("");
                break;
            case R.id.attachmentImageView:
                mDialog = new Dialog(getActivity());
                mDialog.setTitle(R.string.choose_file);
                mDialog.setContentView(R.layout.attachment_dialog);
                mDialog.show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void onReceiveNewMessage(Intent intent){
        // Обработка broadcast с новым сообщением
        // Создание нового объекта ChatMessage, добавление в массив и обновление ListView
        mChatRequest.downloadOneMessage(intent.getStringExtra(LongPollService.NEW_MESSAGE_MESSAGE_ID));
    }

    private void onReceiveUserWrites(){
        // По сигналу от LongPollService ставим надпись "Пользователь набирает сообщение..."
        mTextViewUserWrites.setText(R.string.userWrites);
    }

    private void onReceiveUserStopWrites(){
        // По сигналу от LongPollService стираем надпись "Пользователь набирает сообщение..."
        mTextViewUserWrites.setText("");
    }

    private void onReceiveInputRead(Intent intent){
        // Получаем сигнал из LongPollService о прочтении входящих сообщений
        // Проверяем все объекты ChatMessage из массива mMessageArray с id ниже указанного(localId) и с пометкой "входящие"(isOut() == false)
        // меняем поле read на true и перезагружаем ListView
        ListIterator<ChatMessage> iterator = mMessageArray.listIterator();
        int localId = Integer.valueOf(intent.getStringExtra(LongPollService.LOCAL_ID_INPUT_MESSAGE_KEY));
        while (iterator.hasNext()){
            ChatMessage message = iterator.next();
            if (!message.isRead() && !message.isOut() && message.getId() <= localId){
                message.setReadState(true);
                iterator.set(message);
            }
        }
        mChatAdapter.notifyDataSetChanged();
    }

    private void onReceiveOutputRead(Intent intent){
        // Получаем сигнал из LongPollService о прочтении исходящих сообщений
        // Проверяем все объекты ChatMessage из массива mMessageArray с id ниже указанного(localId) и с пометкой "исходящие"(isOut() == true)
        // меняем поле read на true и перезагружаем ListView
        int localId = Integer.valueOf(intent.getStringExtra(LongPollService.LOCAL_ID_OUTPUT_MESSAGE_KEY));
        ListIterator<ChatMessage> iterator = mMessageArray.listIterator();
        while (iterator.hasNext()){
            ChatMessage message = iterator.next();
            if (!message.isRead() && message.isOut() && message.getId() <= localId){
                message.setReadState(true);
                iterator.set(message);
            }
        }
        mChatAdapter.notifyDataSetChanged();
    }

    public void setUserId(String userId){
        mUserId = userId;
    }

    @Override
    public void onNewMessagesResponse(int totalMessageCount) {
        // Новые сообщения подгружены(ChatRequest)
        // Меняем переменные и опускаем ListView на 50 пунктов вниз
        this.totalMessageCount = totalMessageCount;
        totalDownloadedMessageCount = mMessageArray.size();
        onScrollState = true;
        mListView.setSelection(Integer.valueOf(CHAT_SIZE));
    }

    @Override
    public void onNewMessageResponse() {
        // Новое сообщение добавлено(ChatRequest)
        mListView.setSelection(100500);
    }

}
