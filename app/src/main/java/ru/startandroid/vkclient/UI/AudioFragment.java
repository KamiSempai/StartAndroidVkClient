package ru.startandroid.vkclient.UI;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.startandroid.vkclient.adapters.AudioAdapter;

/**
 * Фрагмент со списком аудиозаписей
 */
public class AudioFragment extends ListFragment {

    ArrayList<Map<String,String>> mAudioList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAudioList = new ArrayList<Map<String,String>>();
        VKRequest vkRequest = new VKRequest("audio.get", VKParameters.from(VKApiConst.OWNER_ID, VKSdk.getAccessToken().userId, VKApiConst.COUNT, "500"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("myLogs","oComp");
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray audioArray = jsonObjectResponse.getJSONArray("items");
                    for (int i = 0; i < audioArray.length(); i++){
                        Map<String,String> oneAudioMap = new HashMap<String, String>();
                        oneAudioMap.put("id",audioArray.getJSONObject(i).getString("id"));
                        oneAudioMap.put("artist",audioArray.getJSONObject(i).getString("artist"));
                        oneAudioMap.put("title",audioArray.getJSONObject(i).getString("title"));
                        oneAudioMap.put("url",audioArray.getJSONObject(i).getString("url"));
                        mAudioList.add(oneAudioMap);

                    }
                    setListView();




                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                Log.d("myLogs","failed");
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                Log.d("myLogs","error");
            }
        });

    }

    private void setListView(){
        AudioAdapter audioAdapter = new AudioAdapter(getActivity(),mAudioList);
        setListAdapter(audioAdapter);

    }




}
