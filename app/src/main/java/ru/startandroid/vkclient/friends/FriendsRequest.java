package ru.startandroid.vkclient.friends;

import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

/**
 * @author Samofal Vitaliy
 * Запросы на получение данных пользователю.
 */
public class FriendsRequest {
    private VKRequest vkRequest;
    private int countOfUsers;
    private FriendsAdapter adapter;
    private FriendsArray friendsArray = new FriendsArray();
    // Устанавливает значение запроса.
    private void setVkRequest(GeneralFriendsFields generalFriendsFields) {
        boolean isCountAvailable = countOfUsers == -1;
        switch (generalFriendsFields) {
            case FIRSTNAME_LASTNAME_ICON_ONLINE:
                if (isCountAvailable) {
                    this.vkRequest = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,photo_100,online"));
                } else {
                    this.vkRequest = VKApi.friends().get(VKParameters.from(
                            VKApiConst.FIELDS, "id,first_name,last_name,photo_100,online"
                            , VKApiConst.COUNT, countOfUsers));
                }
            break;
        }
    }

    public FriendsRequest(GeneralFriendsFields generalFriendsFields,FriendsAdapter adapter,FriendsArray friendsArray) {
        this(generalFriendsFields, adapter,friendsArray , -1);
    }

    public FriendsRequest(GeneralFriendsFields generalFriendsFields, FriendsAdapter adapter, FriendsArray friendsArray, int countOfUsers) {
        this.adapter = adapter;
        this.countOfUsers = countOfUsers;
        this.friendsArray = friendsArray;
        setVkRequest(generalFriendsFields);
        vkRequest.executeWithListener(new RequestListener());
    }
    // Слушатель для запроса друзей
    private class RequestListener extends VKRequest.VKRequestListener{
        // Ассинхронный метод, вызывается по завершению запроса.
        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            for (VKApiUserFull currentUser : (VKUsersArray) response.parsedModel) {
                friendsArray.add(new FriendBuilder(currentUser.id,currentUser.first_name, currentUser.last_name,currentUser.online));
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            super.attemptFailed(request, attemptNumber, totalAttempts);
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
            Log.e("Internal VK Error", error.toString());
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
            super.onProgress(progressType, bytesLoaded, bytesTotal);
            // TODO
        }
    }

}
