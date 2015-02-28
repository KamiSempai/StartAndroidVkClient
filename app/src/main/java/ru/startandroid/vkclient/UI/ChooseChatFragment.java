package ru.startandroid.vkclient.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ru.startandroid.vkclient.R;

/**
 * Created by pc on 26.01.2015.
 */
public class ChooseChatFragment extends Fragment implements View.OnClickListener{

    Button mChatButton;
    EditText mChatEditText;
    ChooseChatFragmentListener mChooseChatFragmentListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ChooseChatFragmentListener){
            mChooseChatFragmentListener = (ChooseChatFragmentListener) activity;
        }else{
            throw new IllegalArgumentException("MainActivity should implement ChooseChatFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, null);
        mChatButton = (Button)view.findViewById(R.id.bt_message);
        mChatButton.setOnClickListener(this);
        mChatEditText = (EditText)view.findViewById(R.id.et_message);
        return view;
    }

    @Override
    public void onClick(View v) {
        mChooseChatFragmentListener.eventFromFragmentListMessages(mChatEditText.getText().toString());
    }

    public interface ChooseChatFragmentListener{
        public void eventFromFragmentListMessages(String id);
    }


}
