package ru.startandroid.vkclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.UI.PhotoFragment;

/**
 * Адаптер для PhotoFragment, в который загружается список альбомов
 */
public class PhotoAdapter extends BaseAdapter {

    private ArrayList<PhotoFragment.Album> mAlbumArrayList;
    private LayoutInflater mInflater;
    private Context mContext;

    public PhotoAdapter(Context context, ArrayList<PhotoFragment.Album> albumArrayList){
        mAlbumArrayList = albumArrayList;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mAlbumArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlbumArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAlbumArrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = mInflater.inflate(R.layout.my_photos_list_item,parent,false);
        }
        ((TextView) view.findViewById(R.id.titleTextView)).setText(mAlbumArrayList.get(position).getTitle());
        ((TextView) view.findViewById(R.id.countTextView)).setText(mAlbumArrayList.get(position).getSize());
        ImageView albumImageView = (ImageView) view.findViewById(R.id.albumPhotoImageView);
        albumImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(mContext).load(mAlbumArrayList.get(position).getImageUrl()).into(albumImageView);
        return view;
    }


}
