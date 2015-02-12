package ru.startandroid.vkclient;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * FrameLayout с кнопкой, который вешается как onTouchListener на ListView с чатом
 * Скрывается при прокрутке вниз, всплывает при прокрутке вверх
 */
public class AutoHideableLayout extends HideableLayout implements View.OnTouchListener {

    float oldY;

    public AutoHideableLayout(Context context) {
        super(context);
    }

    public AutoHideableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHideableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float newY =  event.getY();
        switch(event.getAction()){
            case 2:
                if (newY<oldY){
                    showLayout();
                }else{
                    hideLayout();
                }
        }
        oldY=newY;
        return false;
    }
}
