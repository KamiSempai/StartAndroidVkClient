package ru.startandroid.vkclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * FrameLayout с кнопкой
 */
public class HideableLayout extends FrameLayout {

    public HideableLayout(Context context) {
        super(context);
    }

    public HideableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HideableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HideableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void hideLayout(){
        // скрываем кнопку
        this.animate().translationX(300).setDuration(500);

    }
    protected void showLayout(){
        // показываем кнопку
        this.animate().translationX(0).setDuration(500);
    }



}
