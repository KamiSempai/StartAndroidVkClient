package ru.startandroid.vkclient.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import ru.startandroid.vkclient.adapters.DocAdapter;

/**
 * Created by pc on 25.02.2015.
 */
public class DocFragment extends ListFragment {

    ArrayList<Map<String,String>> mDocList;

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDocList = new ArrayList<Map<String,String>>();
        VKRequest vkRequest = new VKRequest("docs.get", VKParameters.from(VKApiConst.OWNER_ID, VKSdk.getAccessToken().userId, VKApiConst.COUNT, "500"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("myLogs", "oCompdoc");
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray docArray = jsonObjectResponse.getJSONArray("items");
                    for (int i = 0; i < docArray.length(); i++){
                        Map<String,String> oneDocMap = new HashMap<String, String>();
                        oneDocMap.put("id",docArray.getJSONObject(i).getString("id"));
                        oneDocMap.put("size",docArray.getJSONObject(i).getString("size"));
                        oneDocMap.put("title",docArray.getJSONObject(i).getString("title"));
                        mDocList.add(oneDocMap);

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
        Log.d("myLogs","setadapter");
        DocAdapter docAdapter = new DocAdapter(getActivity(),mDocList);
        setListAdapter(docAdapter);

    }
}
