package ru.startandroid.vkclient;

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
import java.util.LinkedList;
import ru.startandroid.vkclient.activities.MainActivity;

/**
 * Запрос на получение истории сообщений данного пользователя
 */
public class ChatRequest {

    String mUserId;
    String mCount;
    LinkedList<ChatMessage> mMessageArray;
    private ChatAdapter mChatAdapter;
    private Context mContext;

    public  ChatRequest (Context context,String userId,String count, LinkedList<ChatMessage> messageArray, ChatAdapter chatAdapter){
        mContext = context;
        mUserId = userId;
        mCount = count;
        mMessageArray = messageArray;
        mChatAdapter = chatAdapter;
    }

    public void downloadMessageHistory(){
        // Отправляем запрос, в onComplete заполняем массив объектами ChatMessage
        VKRequest vkRequest = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID,mUserId,VKApiConst.COUNT,mCount));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    ArrayList<Integer> unreadMessages = new ArrayList<Integer>();// Создаем массив для id непрочитанных сообщений, чтобы пометить их прочитанными
                    for (int i=0; i<messageArray.length(); i++){
                        JSONObject oneMessageObject = messageArray.getJSONObject(i);
                        if(oneMessageObject.getInt("read_state")!=0){ // Если сообщение не прочитано, добавляем его id в unreadMessages
                            unreadMessages.add(oneMessageObject.getInt("id"));
                        }
                        mMessageArray.addFirst(new ChatMessage()
                                .setBody(oneMessageObject.getString("body"))
                                .setId(oneMessageObject.getInt("id"))
                                .setOut(oneMessageObject.getInt("out")!=0 ? true : false )
                                .setReadState(oneMessageObject.getInt("read_state")!=0 ? true : false ));
                    }
                    sendMarkAsRead(unreadMessages);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mChatAdapter.notifyDataSetChanged();


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
                // Тут пока ничего не пишем, сигнал о том, что сообщение отправлено, придет из LongPoolService в onReceive
                // Там сообщение и добавим к списку
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Toast.makeText(mContext, "Сообщение не отправлено", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Toast.makeText(mContext,"Сообщение не отправлено",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMarkAsRead(int unreadMessage){
        sendMarkAsRead(String.valueOf(unreadMessage));
    }

    public void sendMarkAsRead(ArrayList<Integer> unreadMessages){
        if (unreadMessages.size()==0)
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





}