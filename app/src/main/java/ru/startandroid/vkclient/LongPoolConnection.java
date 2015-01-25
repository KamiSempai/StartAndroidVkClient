package ru.startandroid.vkclient;

import android.content.Context;
import android.content.Intent;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * Created by pc on 25.01.2015.
 */
public class LongPoolConnection {

    public static final String START_SERVICE_ACTION = "ru.startandroid.vkclient.START_SERVICE_ACTION";

    Context context;

    public LongPoolConnection(Context context){
        this.context = context;
    }

    public void connect(){
        // Отправка запроса на данные для подключения к LongPool серверу
        // По получении запускаем LongPoolService и передаем ему данные
        VKRequest request = new VKRequest("messages.getLongPollServer", null);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                context.startService(new Intent(context, LongPoolService.class)
                        .setAction(START_SERVICE_ACTION)
                        .putExtra("json", response.json.toString()));
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
