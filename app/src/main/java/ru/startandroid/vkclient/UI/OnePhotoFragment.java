package ru.startandroid.vkclient.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import ru.startandroid.vkclient.R;

/**
 * Фрагмент для отображения фотографии, выбранной в AlbumFragment
 */
public class OnePhotoFragment extends Fragment {

    HashMap<String,String> mOnePhotoMap;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_photo_fragment,null);
        ImageView photoImageView = (ImageView) view.findViewById(R.id.onePhotoImageView);
        Picasso.with(getActivity()).load(mOnePhotoMap.get("photo_604")).into(photoImageView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setPhotoUrl(HashMap<String,String> onePhotoMap){
        mOnePhotoMap = onePhotoMap;

    }


}
