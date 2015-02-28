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
 * Адаптер для загрузки документов в DocFragment
 */
public class DocAdapter extends BaseAdapter implements View.OnClickListener{

    private ArrayList<Map<String,String>> mDocList;
    private LayoutInflater mInflater;
    private Context mContext;
    private DocAdapterListener mDocAdapterListener;




    public DocAdapter(Activity activity, ArrayList<Map<String,String>> docList){
        mDocList = docList;
        mContext = activity.getApplicationContext();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (activity instanceof DocAdapterListener){
            mDocAdapterListener = (DocAdapterListener) activity;
        }else{
            throw new IllegalArgumentException("ResourcePickerActivity should implement DocAdapterListener");
        }
    }

    @Override
    public int getCount() {
        return mDocList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDocList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view==null){
            view = mInflater.inflate(R.layout.item_doc_fragment,parent,false);
        }
        ((TextView) view.findViewById(R.id.textViewTitle)).setText(mDocList.get(position).get("title"));
        ((TextView) view.findViewById(R.id.textViewSize)).setText(ChatAdapter.byteToString(mDocList.get(position).get("size")));
        FrameLayout addDocLayout = (FrameLayout) view.findViewById(R.id.container_doc);
        addDocLayout.setTag(mDocList.get(position));
        addDocLayout.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        mDocAdapterListener.onClickDoc((HashMap<String, String>) v.getTag());
    }

    public interface DocAdapterListener{
        public void onClickDoc(HashMap<String,String> oneDocMap);
    }
}
