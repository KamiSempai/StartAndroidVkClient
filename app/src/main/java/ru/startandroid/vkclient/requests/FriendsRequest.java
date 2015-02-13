package ru.startandroid.vkclient.requests;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import ru.startandroid.vkclient.GeneralFriendsFields;

/**
 * @author Samofal Vitaliy
 * Запросы на получение данных пользователю.
 */
public class FriendsRequest {
    private VKRequest vkRequest;
    private int countOfUsers;
    private int offset;
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
                            , VKApiConst.OFFSET, offset, VKApiConst.COUNT, countOfUsers));
                }
            break;
        }
    }

    private void setVkRequest(int offset, int countOfUsers){
        this.vkRequest = VKApi.friends().get(VKParameters.from(
                VKApiConst.FIELDS, "id,first_name,last_name,photo_100,online"
                , VKApiConst.OFFSET, offset, VKApiConst.COUNT, countOfUsers));
    }

    public FriendsRequest(GeneralFriendsFields generalFriendsFields) {
        this(generalFriendsFields, -1,-1);
    }

    public FriendsRequest(GeneralFriendsFields generalFriendsFields, int offset, int countOfUsers) {
        this.offset = offset;
        this.countOfUsers = countOfUsers;
        setVkRequest(generalFriendsFields);
    }


    public void executeWithListener(VKRequest.VKRequestListener requestListener){
        vkRequest.executeWithListener(requestListener);
    }
    public void executeWithListener(VKRequest.VKRequestListener requestListener,int offset, int countOfUsers){
        setVkRequest(offset,countOfUsers);
        vkRequest.executeWithListener(requestListener);
    }



}
