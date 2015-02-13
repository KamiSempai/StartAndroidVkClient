package ru.startandroid.vkclient.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import ru.startandroid.vkclient.UI.MainActivity;
import ru.startandroid.vkclient.R;

/**
 * Created by pc on 24.01.2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver implements SoundPool.OnLoadCompleteListener {

    private final String GCM_ACTION = "com.google.android.c2dm.intent.RECEIVE";
    private final int NOTIFY_ID = 123;
    private String mMessage;
    private String mFirst_name;
    private String mLast_name;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Получаем broadcast, извлекаем сообщение и id отправителя
        // Отправляем запрос на vk.com на данные отправителя, достаем из результата имя и фамилию
        // Запускаем метод sendNotification()
        if (!intent.getAction().equals(GCM_ACTION))
            return;
        mContext = context.getApplicationContext();
        mMessage = intent.getStringExtra("text");
        new VKRequest("users.get", VKParameters.from("user_ids", intent.getStringExtra("uid")))
                .executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            mFirst_name = response.json.getJSONArray("response").getJSONObject(0).getString("first_name");
                            mLast_name = response.json.getJSONArray("response").getJSONObject(0).getString("last_name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendNotification();
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

    private void sendNotification() {
        // Формируем строку уведомления и отправляем уведомление в статус-бар
        String nString = mLast_name + " " + mFirst_name + ": " + mMessage;
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);// С флагами не разобрался какой лучше
        Notification notification = new Notification.Builder(mContext).setContentIntent(pIntent)
                .setSmallIcon(R.drawable.message)
                .setWhen(System.currentTimeMillis())
                .setContentText(nString)
                .setAutoCancel(true)
                .getNotification();
        nm.notify(NOTIFY_ID, notification);
        sound();
    }


    public void sound(){
        // Создаем звуковое оповещение
        SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(this);
        sp.load(mContext, R.raw.zvuk, 1);
    }


    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        soundPool.play(sampleId,1,1,0,0,1);
    }
}
