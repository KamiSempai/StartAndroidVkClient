package ru.startandroid.vkclient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.io.IOException;


/**
 * Created by pc on 25.01.2015.
 */
public class MyGCM implements GCM{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;
    Context context;
    Activity activity;
    String SENDER_ID = "536340172064";
    String regid;

    public MyGCM(Activity activity){
        this.activity = activity;
        context = activity.getApplicationContext();
    }


    @Override
    public void registerDevice() {
        // Подписка устройства
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);
            register(regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }

        } else {
            Log.i("myLogs", "No valid Google Play Services APK found.");
        }
    }

    @Override
    public void unRegisterDevice() {
        // Отписка устройства
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);
            unregister(regid);
            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i("myLogs", "No valid Google Play Services APK found.");

        }
    }

    private boolean checkPlayServices() {
        // Проверка устройства на наличие Google Play Services APK
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("myLogs", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        // Получение registration ID
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("myLogs", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("myLogs", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        // Сохранение registration ID
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("myLogs", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void register(String regId) {
        // Отправка запроса на подписку устройства на vk.com
        Log.d("myLogs", "sendID="+regId);
        VKRequest request1 = new VKRequest("account.registerDevice", VKParameters.from("token", regId));
        request1.executeWithListener(new VKRequest.VKRequestListener(){

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber,
                                      int totalAttempts) {
                // TODO Auto-generated method stub
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onComplete(VKResponse response) {
                // TODO Auto-generated method stub
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
                // TODO Auto-generated method stub
                super.onError(error);
            }


        });


    }

    private void unregister(String regId) {
        // Запрос на отписку устройства на vk.com
        VKRequest request1 = new VKRequest("account.unregisterDevice", VKParameters.from("token", regId));
        request1.executeWithListener(new VKRequest.VKRequestListener(){

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber,
                                      int totalAttempts) {
                // TODO Auto-generated method stub
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onComplete(VKResponse response) {
                // TODO Auto-generated method stub
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
                // TODO Auto-generated method stub
                super.onError(error);
            }


        });

    }

    private void registerInBackground(){
        new AsyncSay().execute(null,null,null);
    }

    public class AsyncSay extends AsyncTask<Void,Void,String> {
        // Регистрация приложения на сервере GCM
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
                register(regid);
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            // TODO Auto-generated method stub
        }

    }




}
