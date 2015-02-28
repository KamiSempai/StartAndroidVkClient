package ru.startandroid.vkclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.startandroid.vkclient.R;


/**
 * Адаптер для загрузки аудиозаписей в AudioFragment
 */
public class AudioAdapter extends BaseAdapter implements View.OnClickListener{

    private ArrayList<Map<String,String>> mAudioList;
    private LayoutInflater mInflater;
    private Context mContext;
    private AudioAdapterListener mAudioAdapterListener;

    public AudioAdapter(Activity activity, ArrayList<Map<String,String>> audioList){
        mAudioList = audioList;
        mContext = activity.getApplicationContext();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (activity instanceof AudioAdapterListener){
            mAudioAdapterListener = (AudioAdapterListener) activity;
        }else{
            throw new IllegalArgumentException("ResourcePickerActivity should implement AudioAdapterListener");
        }
    }

    @Override
    public int getCount() {
        return mAudioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAudioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = mInflater.inflate(R.layout.item_audio_fragment,parent,false);
        }
        ((TextView) view.findViewById(R.id.textViewSong)).setText(mAudioList.get(position).get("title"));
        ((TextView) view.findViewById(R.id.textViewBand)).setText(mAudioList.get(position).get("artist"));
        FrameLayout addAudioLayout = (FrameLayout) view.findViewById(R.id.container_audio);
        addAudioLayout.setTag(mAudioList.get(position));
        addAudioLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        mAudioAdapterListener.onClickAudio((HashMap<String, String>) v.getTag());
    }

    public interface AudioAdapterListener{
        public void onClickAudio(HashMap<String,String> oneAudioMap);
    }
}
