package ru.startandroid.vkclient.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
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
import ru.startandroid.vkclient.adapters.PhotoAdapter;

/**
 * Фрагмент со списком альбомов пользователя
 */
public class PhotoFragment extends ListFragment  {

    private int mTotalSize;
    private ArrayList<Album> mAlbumArrayList;
    private PhotoFragmentListener mPhotoFragmentListener;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Загрузка списка альбомов пользователя
        VKRequest vkRequest = new VKRequest("photos.getAlbums", VKParameters.from(VKApiConst.OWNER_ID, VKSdk.getAccessToken().userId,"need_system","1","need_covers","1","photo_sizes","1"));
        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                mAlbumArrayList = new ArrayList<Album>();
                try {
                    JSONObject jsonObjectResponse = response.json.getJSONObject("response");
                    JSONArray messageArray = jsonObjectResponse.getJSONArray("items");
                    for(int i = 0; i < messageArray.length(); i++){
                        JSONObject albumJSONObject = messageArray.getJSONObject(i);
                        JSONArray sizesJSONArray = albumJSONObject.getJSONArray("sizes");
                        JSONObject photoJSONObject = sizesJSONArray.getJSONObject(2);
                        mAlbumArrayList.add(new Album()
                                .setTitle(albumJSONObject.getString("title"))
                                .setSize("Колличество фотографий: " + albumJSONObject.getInt("size"))
                                .setImageUrl(photoJSONObject.getString("src"))
                                .setId(albumJSONObject.getLong("id")));
                        mTotalSize+=albumJSONObject.getInt("size");
                    }
                    mAlbumArrayList.add(0,new Album()
                            .setTitle("Все фотографии")
                            .setSize("Колличество фотографий: " + mTotalSize)
                            .setImageUrl(messageArray.getJSONObject(1).getJSONArray("sizes").getJSONObject(2).getString("src"))
                            .setId(-100500));
                    setListView();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PhotoFragmentListener){
            mPhotoFragmentListener = (PhotoFragmentListener) activity;
        }else{
            throw new IllegalArgumentException("ResourcePickerActivity should implement PhotoFragmentListener");
        }
    }

    private void setListView(){
        PhotoAdapter photoAdapter = new PhotoAdapter(getActivity(),mAlbumArrayList);
        setListAdapter(photoAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mPhotoFragmentListener.onClickAlbum(id);
    }

    public class Album {

        private String title;
        private String imageUrl;
        private String size;
        private long id;

        public long getId() {
            return id;
        }

        public Album setId(long id) {
            this.id = id;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Album setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Album setImageUrl(String image) {
            this.imageUrl = image;
            return this;
        }

        public String getSize() {
            return size;
        }

        public Album setSize(String size) {
            this.size = size;
            return this;
        }
    }

    public interface PhotoFragmentListener {
        public void onClickAlbum(long id);
    }
}
