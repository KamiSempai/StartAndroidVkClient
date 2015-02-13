package ru.startandroid.vkclient.gcm;

import android.content.Context;
import android.content.Intent;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * Created by pc on 25.01.2015.
 */
public class LongPollConnection {

    public static final String START_SERVICE_ACTION = "ru.startandroid.vkclient.START_SERVICE_ACTION";

    static Context mContext;

    public static void connect(Context context){
        // Отправка запроса на данные для подключения к LongPool серверу
        // По получении запускаем LongPollService и передаем ему данные
        mContext = context;
        VKRequest request = new VKRequest("messages.getLongPollServer", null);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                mContext.startService(new Intent(mContext, LongPollService.class)
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
