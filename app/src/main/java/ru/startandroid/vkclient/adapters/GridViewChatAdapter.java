package ru.startandroid.vkclient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Адаптер для GridView, в который подгружаются фотографии чата
 */
public class GridViewChatAdapter extends BaseAdapter {

    Context mContext;
    private ArrayList<String> mUrlArray;

    public GridViewChatAdapter(Context context, ArrayList<String> urlArray){
        mContext = context;
        mUrlArray = urlArray;
    }

    @Override
    public int getCount() {
        return mUrlArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrlArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(mUrlArray.get(position)).into(imageView);
        return imageView;
    }
}
