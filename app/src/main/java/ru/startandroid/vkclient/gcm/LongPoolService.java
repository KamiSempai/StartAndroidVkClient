package ru.startandroid.vkclient.gcm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.httpClient.VKJsonOperation;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.startandroid.vkclient.activities.MainActivity;
import ru.startandroid.vkclient.gcm.LongPoolConnection;


/**
 * Created by pc on 24.01.2015.
 */
public class LongPoolService extends Service {

    public static final String NEW_MESSAGE_SERVICE_ACTION = "ru.startandroid.vkclient.NEW_MESSAGE_SERVICE_ACTION";
    public static final String USER_ONLINE_SERVICE_ACTION = "ru.startandroid.vkclient.USER_ONLINE_SERVICE_ACTION";
    public static final String USER_OFFLINE_SERVICE_ACTION = "ru.startandroid.vkclient.USER_OFFLINE_SERVICE_ACTION";
    public static final String USER_WRITES_SERVICE_ACTION = "ru.startandroid.vkclient.USER_WRITES_SERVICE_ACTION";

    //Ключи
    public static final String NEW_MESSAGE_USER_ID_KEY = "NEW_MESSAGE_USER_ID_KEY";
    public static final String NEW_MESSAGE_FLAG_KEY = "NEW_MESSAGE_FLAG_KEY";
    public static final String NEW_MESSAGE_MESSAGE_ID = "NEW_MESSAGE_MESSAGE_ID ";
    public static final String NEW_MESSAGE_TEXT_KEY = "NEW_MESSAGE_TEXT_KEY";

    public static final String USER_ONLINE_USER_ID_KEY = "USER_ONLINE_USER_ID_KEY";
    public static final String USER_OFFLINE_USER_ID_KEY = "USER_OFFLINE_USER_ID_KEY";
    public static final String USER_WRITES_USER_ID_KEY = "USER_WRITES_USER_ID_KEY";

    BroadcastReceiver mBroadcastReceiver;
    VKJsonOperation mVkJsonOperation;
    String mTs, mServer, mKey;
    AsyncTaskLoader mAsyncTaskLoader;


    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MainActivity.DESTROY_SERVICE_ACTION)){
                    // Broadcast приходит после унижтожения MainActivity - уничтожаем сервис
                    stopSelf();
                }

            }
        };
        IntentFilter intFilt = new IntentFilter();
        intFilt.addAction(MainActivity.DESTROY_SERVICE_ACTION);
        registerReceiver(mBroadcastReceiver, intFilt);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        // Получаем данные для подключения к LongPool серверу и устанавливаем связь
        if (intent.getAction().equals(LongPoolConnection.START_SERVICE_ACTION)){

            try {
                extractJSON(intent.getStringExtra("json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAsyncTaskLoader.cancel(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void extractJSON(String jsonString) throws JSONException {
        // Достаем из полученного объекта 3 строки, формируем из них url для соединения и грузим
        JSONObject jsonObject = new JSONObject(jsonString);
        mTs = jsonObject.getJSONObject("response").getString("ts");
        mServer = jsonObject.getJSONObject("response").getString("server");
        mKey = jsonObject.getJSONObject("response").getString("key");
        loadLongPoolUrl(createLongPoolUrl(mServer, mKey, mTs));
    }

    private String createLongPoolUrl(String server,String key,String ts){
        return "http://"+server+"?act=a_check&key="+key+"&ts="+ts+"&wait=15&mode=2";
    }

    private void loadLongPoolUrl(String url){
        // Соединяемся с сервером с помощью класса VKJsonOperation в отдельном потоке
        mVkJsonOperation = new VKJsonOperation(new HttpGet(url));
        mVkJsonOperation.setJsonOperationListener(new VKJsonOperation.VKJSONOperationCompleteListener() {
            @Override
            public void onComplete(VKJsonOperation operation, JSONObject response) {
                super.onComplete(operation, response);
                // Обрабываем ответ
                extractResponse(response);
            }

            @Override
            public void onError(VKJsonOperation operation, VKError error) {
                super.onError(operation, error);
                // Если ошибка, снова получаем данные(ts,server,key) для соединения с сервером
                // (Это еще не тестировал - когда дописал, как назло, ошибки прекратились)
                connectAfterError();
            }
        });
        asyncStart(mVkJsonOperation);
    }

    private void connectAfterError(){
        new LongPoolConnection(this).connect();
    }

    private void asyncStart(VKJsonOperation op){
        mAsyncTaskLoader = new AsyncTaskLoader();
        mAsyncTaskLoader.execute(op);
    }

    private void extractResponse(JSONObject jsonObject){
        // Обрабатывем полученные данные и отправляем broadcastы с событиями в MainActivity
        // В ответе приходит - новый ts, переписываем url, снова грузим и так по кругу

        try {
            mTs = jsonObject.getString("ts");
            if (!jsonObject.isNull("updates")){
                JSONArray jsonArray = jsonObject.getJSONArray("updates");
                for(int i=0;i<jsonArray.length();i++ ){
                    JSONArray myArray = jsonArray.getJSONArray(i);
                    int z = myArray.getInt(0);
                    switch (z){
                        case 4:
                            // Массив вида [4,$message_id,$flags,$from_id,$timestamp,$subject,$text,$attachments]
                            // Добавление нового сообщения - 8 элементов
                            // Берем 2-й(id сообщения), 3-й(флаги), 4-й(id отправителя) и 7-й(текст сообщения) и отправляем в MainActivity
                            sendBroadcast(new Intent()
                                    .setAction(NEW_MESSAGE_SERVICE_ACTION)
                                    .putExtra(NEW_MESSAGE_USER_ID_KEY,myArray.getString(3))
                                    .putExtra(NEW_MESSAGE_FLAG_KEY,Integer.valueOf(myArray.getString(2)))
                                    .putExtra(NEW_MESSAGE_TEXT_KEY,myArray.getString(6))
                                    .putExtra(NEW_MESSAGE_MESSAGE_ID,myArray.getString(1))

                            );
                            break;
                        case 8:
                            // Массив вида [8,-$user_id,$extra]
                            // Друг стал онлайн - 3 элемента
                            // Отправляем 2-й - id друга
                            sendBroadcast(new Intent()
                                            .setAction(USER_ONLINE_SERVICE_ACTION)
                                            .putExtra(USER_ONLINE_USER_ID_KEY,myArray.getString(1).replace("-",""))
                            );
                            break;
                        case 9:
                            // Массив вида [9,-$user_id,$flags]
                            // Друг ушел в оффлайн - 3 элемента
                            // Отправляем 2-й - id друга
                            sendBroadcast(new Intent()
                                            .setAction(USER_OFFLINE_SERVICE_ACTION)
                                            .putExtra(USER_OFFLINE_USER_ID_KEY,myArray.getString(1).replace("-",""))
                            );
                            break;
                        case 61:
                            // Массив вида [61,$user_id,$flags]
                            // Пользователь начал набирать текст в диалоге - 3 элемента
                            // Отправляем 2-й - id пользователя
                            sendBroadcast(new Intent()
                                            .setAction(USER_WRITES_SERVICE_ACTION)
                                            .putExtra(USER_WRITES_USER_ID_KEY,myArray.getString(1).replace("-",""))
                            );
                            break;
                    }
                }
            }

            loadLongPoolUrl(createLongPoolUrl(mServer, mKey, mTs));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class AsyncTaskLoader extends AsyncTask<VKJsonOperation,Void,Void>{


        @Override
        protected Void doInBackground(VKJsonOperation... params) {
            VKJsonOperation op = params[0];
            op.start();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
