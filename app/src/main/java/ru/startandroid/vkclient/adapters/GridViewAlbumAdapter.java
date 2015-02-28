package ru.startandroid.vkclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.startandroid.vkclient.R;


/**
 * Адаптер для GridView, в который подгружаются фотографии из альбома для AlbumFragment
 */
public class GridViewAlbumAdapter extends BaseAdapter implements View.OnClickListener {

    Context mContext;
    ArrayList<Map<String,String>> mPhotoUrlList;
    LayoutInflater mInflater;
    GridViewAlbumAdapterListener mGridViewAlbumAdapterListener;

    public GridViewAlbumAdapter(Activity activity, ArrayList<Map<String,String>> photoUrlList){
        mContext = activity.getApplicationContext();
        mPhotoUrlList = photoUrlList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (activity instanceof GridViewAlbumAdapterListener){
            mGridViewAlbumAdapterListener = (GridViewAlbumAdapterListener) activity;
        }else{
            throw new IllegalArgumentException("ResourcePickerActivity should implement GridViewAlbumAdapterListener");
        }
    }

    @Override
    public int getCount() {
        return mPhotoUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhotoUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = mInflater.inflate(R.layout.item_album_gridview,parent,false);
        }
        view.setLayoutParams(new GridView.LayoutParams(150, 150));
        ImageView photoImageView = (ImageView) view.findViewById(R.id.photo_image_view);
        photoImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(mContext).load(mPhotoUrlList.get(position).get("photo_130")).into(photoImageView);
        ImageView pickImageView = (ImageView) view.findViewById(R.id.pick_image_view);
        pickImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        photoImageView.setOnClickListener(this);
        pickImageView.setOnClickListener(this);
        photoImageView.setTag(mPhotoUrlList.get(position));
        pickImageView.setTag(mPhotoUrlList.get(position));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photo_image_view:
                mGridViewAlbumAdapterListener.onClickPhoto((HashMap<String,String>)v.getTag());
                break;
            case R.id.pick_image_view:
                mGridViewAlbumAdapterListener.onClickPick((HashMap<String,String>)v.getTag());
                break;
        }

    }

    public interface GridViewAlbumAdapterListener{
        public void onClickPhoto(HashMap<String,String> onePhotoMap);
        public void onClickPick(HashMap<String,String> onePhotoMap);
    }
}
