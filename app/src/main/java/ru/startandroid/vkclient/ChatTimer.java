package ru.startandroid.vkclient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import ru.startandroid.vkclient.fragments.ChatFragment;

/**
 * Created by pc on 30.01.2015.
 */
public class ChatTimer {

    public static final String ERASE_TEXT_VIEW_USER_WRITES = "ERASE_TEXT_VIEW_USER_WRITES";
    final int DELAY = 5000;
    Timer mTimer;
    TimerTask mTimerTask;
    Context mContext;


    public ChatTimer(Context context){
        mTimer = new Timer();
        mContext = context;
    }

    public void start(){
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mContext.sendBroadcast(new Intent().setAction(ERASE_TEXT_VIEW_USER_WRITES));
            }
        };
        mTimer.schedule(mTimerTask,DELAY);
    }

    public void cancel(){
        if(mTimerTask!=null){
            mTimerTask.cancel();
        }
    }

    public void release(){
        if (mTimerTask !=null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

}
