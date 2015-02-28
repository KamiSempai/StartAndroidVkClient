package ru.startandroid.vkclient.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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

import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.GridViewAlbumAdapter;

/**
 * Created by pc on 25.02.2015.
 */
public class AlbumFragment extends Fragment {

    GridView mGridView;
    String mAlbumId;
    ArrayList<Map<String,String>> mPhotoUrlList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, null);
        mGridView = (GridView) view.findViewById(R.id.albumGridView);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mAlbumId.equals("all")){
            allPhotoRequest();
        }else{
            albumPhotoRequest();
        }


    }

    private void setGridViewAdapter(){
        GridViewAlbumAdapter gridViewAdapter = new GridViewAlbumAdapter(getActivity(),mPhotoUrlList);
        mGridView.setAdapter(gridViewAdapter);
    }

    public void setAlbumId (long id){
        if(id == -6){
            mAlbumId = "profile";
        }else if(id == -7){
            mAlbumId = "wall";
        }else if(id == -15){
            mAlbumId = "saved";
        }else if(id == -100500){
            mAlbumId = "all";
        }else{
            mAlbumId = String.valueOf(id);
        }
    }

    private void albumPhotoRequest(){
        // Загрузка фотографий из конкретного альбома
        VKRequest vkRequest = new VKRequest("photos.get", VKParameters.from(VKApiConst.OWNER_ID, VKSdk.getAccessToken().userId, "album_id",mAlbumId));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                mPhotoUrlList = new ArrayList<Map<String,String>>();

                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    for (int i = 0; i < messageArray.length(); i++){
                        Map onePhotoMap = new HashMap<String,String>();
                        onePhotoMap.put("photo_130",messageArray.getJSONObject(i).getString("photo_130"));
                        onePhotoMap.put("photo_604",messageArray.getJSONObject(i).getString("photo_604"));
                        onePhotoMap.put("id",messageArray.getJSONObject(i).getString("id"));
                        mPhotoUrlList.add(onePhotoMap);

                    }
                    setGridViewAdapter();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });
    }

    private void allPhotoRequest(){
        // Загрузка всех фотографий пользователя
        VKRequest vkRequest = new VKRequest("photos.getAll", VKParameters.from(VKApiConst.OWNER_ID,VKSdk.getAccessToken().userId,VKApiConst.COUNT,"200"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                mPhotoUrlList = new ArrayList<Map<String,String>>();

                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    for (int i = 0; i < messageArray.length(); i++){
                        Map onePhotoMap = new HashMap<String,String>();
                        onePhotoMap.put("photo_130",messageArray.getJSONObject(i).getString("photo_130"));
                        onePhotoMap.put("photo_604",messageArray.getJSONObject(i).getString("photo_604"));
                        onePhotoMap.put("id",messageArray.getJSONObject(i).getString("id"));
                        mPhotoUrlList.add(onePhotoMap);
                    }
                    setGridViewAdapter();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }
        });

    }


}

