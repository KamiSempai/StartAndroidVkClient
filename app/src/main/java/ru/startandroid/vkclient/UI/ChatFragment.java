package ru.startandroid.vkclient.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import ru.startandroid.vkclient.gcm.LongPollService;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.ChatAdapter;
import ru.startandroid.vkclient.ChatMessage;
import ru.startandroid.vkclient.requests.ChatRequest;

/**
 * Фрагмент с чатом
 */
public class ChatFragment extends Fragment implements View.OnClickListener, ChatRequest.OnNewResponseListener {

    public static final String PHOTO_FRAGMENT_ACTION = "ru.startandroid.vkclient.PHOTO_FRAGMENT_ACTION";
    public static final String PHOTO_ID_KEY = "PHOTO_ID_KEY";
    public static final String PHOTO_URL_130_KEY = "PHOTO_URL_130_KEY";
    public static final String PHOTO_URL_604_KEY = "PHOTO_URL_604_KEY";
    public static final String AUDIO_FRAGMENT_ACTION = "ru.startandroid.vkclient.AUDIO_FRAGMENT_ACTION ";
    public static final String AUDIO_ID_KEY = "AUDIO_ID_KEY";
    public static final String AUDIO_ARTIST_KEY = "AUDIO_ARTIST_KEY";
    public static final String AUDIO_TITLE_KEY = "AUDIO_TITLE_KEY";
    public static final String AUDIO_URL_KEY = "AUDIO_URL_KEY";
    public static final String DOC_FRAGMENT_ACTION = "ru.startandroid.vkclient.DOC_FRAGMENT_ACTION ";
    public static final String DOC_ID_KEY = "DOC_ID_KEY";
    public static final String DOC_SIZE_KEY = "DOC_SIZE_KEY";
    public static final String DOC_TITLE_KEY = "DOC_TITLE_KEY";




    View thisView;
    ChatRequest mChatRequest;
    ChatAdapter mChatAdapter;
    BroadcastReceiver mBroadcastReceiver;
    LinkedList<ChatMessage> mMessageArray;
    ArrayList<Map<String,Object>> mAttachmentList;
    LinearLayout mAttachmentLayout;
    ListView mListView;
    EditText mEditText;
    TextView mTextViewUserWrites;
    ImageView mAttachmentImageView,mSendImageView,mPhotosImageView,mAudioImageView,mDocImageView;
    Dialog mDialog;
    String mUserId;
    public static final String CHAT_SIZE = "50";
    public static final int REQUEST_CODE = 1;
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
        thisView = view;

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChatAdapter = new ChatAdapter(getActivity(),mMessageArray,R.layout.item_chat,2);
        mListView.setAdapter(mChatAdapter);
        mChatRequest = new ChatRequest(getActivity(),this,mUserId,CHAT_SIZE,mMessageArray,mChatAdapter);
        mChatRequest.downloadMessages(totalDownloadedMessageCount);
        mAttachmentList = new ArrayList<Map<String,Object>>();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Получение сигнала из ResourcePickerActivity о том, что выбран файл для прикрепления
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            switch (data.getAction()){
                case PHOTO_FRAGMENT_ACTION:
                    Map<String,Object> photoAttachment = new HashMap<String,Object>();
                    photoAttachment.put("type","photo");
                    photoAttachment.put("id",data.getStringExtra(PHOTO_ID_KEY));
                    photoAttachment.put("photo_130",data.getStringExtra(PHOTO_URL_130_KEY));
                    photoAttachment.put("photo_604",data.getStringExtra(PHOTO_URL_604_KEY));
                    addAttachment(photoAttachment);
                    break;
                case AUDIO_FRAGMENT_ACTION:
                    Map<String,Object> audioAttachment = new HashMap<String,Object>();
                    audioAttachment.put("type","audio");
                    audioAttachment.put("id",data.getStringExtra(AUDIO_ID_KEY));
                    audioAttachment.put("artist",data.getStringExtra(AUDIO_ARTIST_KEY));
                    audioAttachment.put("title",data.getStringExtra(AUDIO_TITLE_KEY));
                    addAttachment(audioAttachment);
                    break;
                case DOC_FRAGMENT_ACTION:
                    Map<String,Object> docAttachment = new HashMap<String,Object>();
                    docAttachment.put("type","doc");
                    docAttachment.put("id",data.getStringExtra(DOC_ID_KEY));
                    docAttachment.put("size",data.getStringExtra(DOC_SIZE_KEY));
                    docAttachment.put("title",data.getStringExtra(DOC_TITLE_KEY));
                    addAttachment(docAttachment);
                    break;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendImageView:
                // Отправка сообщения
                mChatRequest.sendMessage(mEditText.getText().toString(), mAttachmentList);
                mEditText.setText("");
                mAttachmentList.clear();
                mAttachmentLayout.removeAllViews();
                break;
            case R.id.attachmentImageView:
                // Вызов диалога
                mDialog = new Dialog(getActivity());
                mDialog.setTitle(R.string.choose_file);
                mDialog.setContentView(R.layout.attachment_dialog);
                mPhotosImageView = (ImageView) mDialog.findViewById(R.id.photoDialogImageView);
                mPhotosImageView.setOnClickListener(this);
                mAudioImageView = (ImageView) mDialog.findViewById(R.id.audioDialogImageView);
                mAudioImageView.setOnClickListener(this);
                mDocImageView = (ImageView) mDialog.findViewById(R.id.docDialogImageView);
                mDocImageView.setOnClickListener(this);
                mDialog.show();
                break;
            case R.id.photoDialogImageView:
                // Нажатие на картинку photo диалога
                startActivityForResult(new Intent(getActivity(),ResourcePickerActivity.class).putExtra("fragment",1),REQUEST_CODE);
                mDialog.cancel();
                break;
            case R.id.audioDialogImageView:
                // Нажатие на картинку audio диалога
                startActivityForResult(new Intent(getActivity(),ResourcePickerActivity.class).putExtra("fragment",2),REQUEST_CODE);
                mDialog.cancel();
                break;
            case R.id.docDialogImageView:
                // Нажатие на картинку doc диалога
                startActivityForResult(new Intent(getActivity(),ResourcePickerActivity.class).putExtra("fragment",3),REQUEST_CODE);
                mDialog.cancel();
                break;
            case R.id.itemCancelPhotoAttachImageView:
                // Сброс photo-прикрепления
                removeViewFromAttachment((View) v.getTag());
                break;
            case R.id.itemCancelAudioAttachImageView:
                // Сброс audio-прикрепления
                removeViewFromAttachment((View) v.getTag());
                break;
            case R.id.itemCancelDocAttachImageView:
                // Сброс doc-прикрепления
                removeViewFromAttachment((View) v.getTag());
                break;
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

    private void addAttachment(Map<String,Object> attachment){
        // Добавление view с прикреплением к AttachmentLayout
        View view;
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAttachmentLayout = (LinearLayout) thisView.findViewById(R.id.attachment_layout);
        switch((String)attachment.get("type")){
            case "photo":
                view = layoutInflater.inflate(R.layout.item_chat_attachment,mAttachmentLayout,false);
                ImageView photoAttachmentImageView = (ImageView) view.findViewById(R.id.itemPhotoAttachImageView);
                ImageView photoCancelAttachmentImageView = (ImageView) view.findViewById(R.id.itemCancelPhotoAttachImageView);
                photoCancelAttachmentImageView.setTag(view);
                photoCancelAttachmentImageView.setOnClickListener(this);
                photoAttachmentImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.with(getActivity()).load((String)attachment.get("photo_130")).into(photoAttachmentImageView);
                mAttachmentLayout.addView(view);
                attachment.put("view",view);
                mAttachmentList.add(attachment);
                break;
            case "audio":
                view = layoutInflater.inflate(R.layout.audio_chat_attachment,mAttachmentLayout,false);
                ((TextView) view.findViewById(R.id.audioBandTextView)).setText((CharSequence) attachment.get("artist"));
                ((TextView) view.findViewById(R.id.audioSongTextView)).setText((CharSequence) attachment.get("title"));
                ImageView audioCancelAttachmentImageView = (ImageView) view.findViewById(R.id.itemCancelAudioAttachImageView);
                audioCancelAttachmentImageView.setTag(view);
                audioCancelAttachmentImageView.setOnClickListener(this);
                mAttachmentLayout.addView(view);
                attachment.put("view",view);
                mAttachmentList.add(attachment);
                break;
            case "doc":
                view = layoutInflater.inflate(R.layout.doc_chat_attachment,mAttachmentLayout,false);
                ((TextView) view.findViewById(R.id.docTitleTextView)).setText((CharSequence) attachment.get("title"));
                ((TextView) view.findViewById(R.id.docSizeTextView)).setText(ChatAdapter.byteToString((String) attachment.get("size")));
                ImageView docCancelAttachmentImageView = (ImageView) view.findViewById(R.id.itemCancelDocAttachImageView);
                docCancelAttachmentImageView.setTag(view);
                docCancelAttachmentImageView.setOnClickListener(this);
                mAttachmentLayout.addView(view);
                attachment.put("view",view);
                mAttachmentList.add(attachment);
                break;
        }
    }

    private void removeViewFromAttachment(View v){
        // Открепление view с прикреплением от AttachmentLayout
        mAttachmentLayout.removeView(v);
        ListIterator<Map<String,Object>> iterator = mAttachmentList.listIterator();
        while (iterator.hasNext()){
            Map<String,Object> oneAttachmentMap= iterator.next();
            if (oneAttachmentMap.get("view") == v){
                iterator.remove();
            }
        }
    }

}
