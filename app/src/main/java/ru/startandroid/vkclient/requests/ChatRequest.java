package ru.startandroid.vkclient.requests;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import ru.startandroid.vkclient.ChatMessage;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.UI.MainActivity;
import ru.startandroid.vkclient.adapters.ChatAdapter;

/**
 * Запрос на получение истории сообщений, отправки сообщений и пометки сообщений прочитанными
 */
public class ChatRequest {

    public static final String VIDEO = "video";
    public static final String PHOTO = "photo";
    public static final String AUDIO = "audio";
    public static final String DOC = "doc";
    public static final String TYPE = "type";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String SIZE = "size";
    public static final String EXT = "ext";
    public static final String PHOTO_604 = "photo_604";
    public static final String PHOTO_320 = "photo_320";

    int mTotalMessageCount;
    String mUserId;
    String mCount;
    LinkedList<ChatMessage> mMessageArray;
    private ChatAdapter mChatAdapter;
    private Context mContext;
    OnNewResponseListener listener; // Слушатель для отправки в ChatFragment сигнала об установке новых данных на ListView

    public  ChatRequest (Context context,OnNewResponseListener listener,String userId,String count, LinkedList<ChatMessage> messageArray, ChatAdapter chatAdapter){
        this.listener = listener;
        mContext = context;
        mUserId = userId;
        mCount = count;
        mMessageArray = messageArray;
        mChatAdapter = chatAdapter;
    }

    public void downloadMessages(final int offset){
        // Отправляем запрос на сообщения чата, в onComplete заполняем массив mMessageArray объектами ChatMessage
        VKRequest vkRequest = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID,mUserId,VKApiConst.COUNT,mCount,VKApiConst.OFFSET,String.valueOf(offset)));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    mTotalMessageCount = jsonObjectResponse.getInt("count");
                    ArrayList<Integer> unreadMessages = new ArrayList<Integer>();// Создаем массив для id непрочитанных сообщений, чтобы пометить их прочитанными
                    for (int i=0; i<messageArray.length(); i++) {
                        JSONObject oneMessageObject = messageArray.getJSONObject(i);
                        if (oneMessageObject.getInt("read_state") != 0) { // Если сообщение не прочитано, добавляем его id в unreadMessages
                            unreadMessages.add(oneMessageObject.getInt("id"));
                        }
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setBody(oneMessageObject.getString("body"))
                                .setId(oneMessageObject.getInt("id"))
                                .setOut(oneMessageObject.getInt("out") != 0 ? true : false)
                                .setReadState(oneMessageObject.getInt("read_state") != 0 ? true : false);
                        if (!oneMessageObject.isNull("attachments")){ // Если есть прикререпление, заполняем поле attachments
                            chatMessage.setAttachments(getAttachments(oneMessageObject.getJSONArray("attachments")));
                        }
                        mMessageArray.addFirst(chatMessage);

                    }

                    sendMarkAsRead(unreadMessages);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mChatAdapter.notifyDataSetChanged();
                listener.onNewMessagesResponse(mTotalMessageCount);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                listener.onNewMessagesResponse(mTotalMessageCount);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                listener.onNewMessagesResponse(mTotalMessageCount);
            }
        });
    }

    public void downloadOneMessage(String messageId){
        // Загрузка одного сообщения
        // Вызывается после уведомления из LP-сервиса о новом сообщении
        VKRequest vkRequest = new VKRequest("messages.getById",VKParameters.from("message_ids",messageId));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    JSONObject oneMessageObject = messageArray.getJSONObject(0);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setBody(oneMessageObject.getString("body"))
                            .setId(oneMessageObject.getInt("id"))
                            .setOut(oneMessageObject.getInt("out") != 0 ? true : false)
                            .setReadState(oneMessageObject.getInt("read_state") != 0 ? true : false);
                    if (!oneMessageObject.isNull("attachments")){ // Если есть прикререпление, заполняем поле attachments
                        chatMessage.setAttachments(getAttachments(oneMessageObject.getJSONArray("attachments")));
                    }
                    mMessageArray.add(chatMessage);
                    sendMarkAsRead(oneMessageObject.getInt("id"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mChatAdapter.notifyDataSetChanged();
                listener.onNewMessageResponse();

            }
        });
    }

    public void sendMessage(String message){
        // Отправка сообщения
        VKRequest vkRequest = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID,mUserId,VKApiConst.MESSAGE,message));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Toast.makeText(mContext, R.string.messageNotSend, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Toast.makeText(mContext,R.string.messageNotSend,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMarkAsRead(int unreadMessage){
        sendMarkAsRead(String.valueOf(unreadMessage));
    }

    public void sendMarkAsRead(ArrayList<Integer> unreadMessages){
        if (unreadMessages.size() == 0)
            return;
        sendMarkAsRead(unreadMessages.toString().replace(" ","").replace("[","").replace("]",""));
    }

    private void sendMarkAsRead(String unreadMessages){
        // Запрос к серверу для пометки сообщений прочитанными
        VKRequest vkRequest = new VKRequest("messages.markAsRead", VKParameters.from("message_ids",unreadMessages,"peer_id",mUserId));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d(MainActivity.LOG_TAG,response.responseString);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });
    }

    private ArrayList<Map<String,String>> getAttachments(JSONArray jsonArray) throws JSONException {
        //Достаем из JSON-объекта массив с прикреплениями и переводим их в ArrayList<Map<String,String>>
        ArrayList<Map<String,String>> attachments = new ArrayList<Map<String,String>>();
        for (int i = 0; i < jsonArray.length(); i++){
                Map<String,String> map = new HashMap<String,String>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                switch (jsonObject.getString(TYPE)){
                    case AUDIO:
                        JSONObject audioJSON = jsonObject.getJSONObject(AUDIO);
                        map.put(TYPE,AUDIO);
                        map.put(ARTIST,audioJSON.getString(ARTIST));
                        map.put(TITLE,audioJSON.getString(TITLE));
                        map.put(URL,audioJSON.getString(URL));
                        attachments.add(map);

                        break;
                    case DOC:
                        JSONObject docJSON = jsonObject.getJSONObject(DOC);
                        map.put(TYPE,DOC);
                        map.put(SIZE,docJSON.getString(SIZE));
                        map.put(TITLE,docJSON.getString(TITLE));
                        map.put(EXT,docJSON.getString(EXT));
                        map.put(URL,docJSON.getString(URL));
                        attachments.add(map);
                        break;
                    case PHOTO:
                        JSONObject photoJSON = jsonObject.getJSONObject(PHOTO);
                        map.put(TYPE,PHOTO);
                        map.put(PHOTO_604,photoJSON.getString(PHOTO_604));
                        attachments.add(map);
                        break;
                    case VIDEO:
                        JSONObject videoJSON = jsonObject.getJSONObject(VIDEO);
                        map.put(TYPE,VIDEO);
                        map.put(PHOTO_320,videoJSON.getString(PHOTO_320));
                        attachments.add(map);
                }
        }
        Collections.sort(attachments, new MapComparator()); // Сортировка прикреплений

        return attachments;
    }


    public interface OnNewResponseListener{
        public void onNewMessagesResponse(int totalMessageCount);
        public void onNewMessageResponse();
    }

    private class MapComparator implements Comparator<Map<String,String>>{
        // Компаратор для сортировки списка с прикреплениями по порядку: photo,video,audio,doc.

        Map<String,Integer> stringToInt;

        @Override
        public int compare(Map<String, String> lhs, Map<String, String> rhs) {
            stringToInt = new HashMap<String,Integer>();
            stringToInt.put(PHOTO,0);
            stringToInt.put(VIDEO,1);
            stringToInt.put(AUDIO,2);
            stringToInt.put(DOC,3);
            int type1 = stringToInt.get(lhs.get(TYPE));
            int type2 = stringToInt.get(rhs.get(TYPE));
            if (type1 > type2){
                return 1;
            }else if(type1 < type2){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
