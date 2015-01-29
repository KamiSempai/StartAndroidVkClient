package ru.startandroid.vkclient;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import ru.startandroid.vkclient.ChatAdapter;
import ru.startandroid.vkclient.ChatMessage;

/**
 * Запрос на получение истории сообщений данного пользователя
 */
public class ChatRequest {

    String mUserId;
    String mCount;
    LinkedList<ChatMessage> mMessageArray;
    private ChatAdapter mChatAdapter;

    public  ChatRequest (String userId,String count, LinkedList<ChatMessage> messageArray, ChatAdapter chatAdapter){
        mUserId = userId;
        mCount = count;
        mMessageArray = messageArray;
        mChatAdapter = chatAdapter;
    }

    public void execute(){
        // Отправляем запрос, в onComplete заполняем массив объектами ChatMessage
        VKRequest vkRequest = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID,mUserId,VKApiConst.COUNT,mCount));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    for (int i=0; i<messageArray.length(); i++){
                        JSONObject oneMessageObject = messageArray.getJSONObject(i);
                        mMessageArray.addFirst(new ChatMessage()
                                .setBody(oneMessageObject.getString("body"))
                                .setId(oneMessageObject.getInt("id"))
                                .setOut(oneMessageObject.getInt("out")!=0 ? true : false )
                                .setReadState(oneMessageObject.getInt("read_state")!=0 ? true : false ));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mChatAdapter.notifyDataSetChanged();


            }
        });
    }

}
