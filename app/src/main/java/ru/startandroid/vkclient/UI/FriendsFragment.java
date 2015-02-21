package ru.startandroid.vkclient.UI;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;
import java.util.ArrayList;
import java.util.List;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.FriendsAdapter;
import ru.startandroid.vkclient.requests.FriendsRequest;
import ru.startandroid.vkclient.GeneralFriendsFields;

/**
 * @author Samofal Vitaliy
 *         Фрагмент отвечающий за сбор информации и отображение друзей.
 */

public class FriendsFragment extends ListFragment {

    public static final String id = "ID";

    private List<VKApiUserFull> mFriendsArray = new ArrayList<>(20);
    private FriendsAdapter mFriendsAdapter;
    private int mOffset;
    private int mStep;
    private Boolean mIsAllUsersDownloaded = false;



    public FriendsFragment(){
        this(0,20);
    }
    public FriendsFragment(int offset,int step){
        mOffset = offset;
        mStep = step;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFriendsAdapter = new FriendsAdapter(getActivity(), R.layout.friends_list, R.id.first_last_name, mFriendsArray);

        final FriendsRequest friendsRequest = new FriendsRequest(GeneralFriendsFields.FIRSTNAME_LASTNAME_ICON_ONLINE, mOffset, mStep);
        friendsRequest.executeWithListener(new VKFriendsRequest(mFriendsAdapter, mFriendsArray));
        getActivity().onRetainNonConfigurationInstance();
        setListAdapter(mFriendsAdapter);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 &&
                        mFriendsAdapter.isUpdatedData() &&
                        !mIsAllUsersDownloaded &&
                        (firstVisibleItem + visibleItemCount) == totalItemCount) {
                    int diffOfCount = (mFriendsAdapter.getCountOfAllUsers() - (mOffset + mStep));
                    mStep = diffOfCount < 20 ? diffOfCount : mStep;
                    mIsAllUsersDownloaded = diffOfCount <= 0;
                    mOffset += 20;
                    friendsRequest.executeWithListener(new VKFriendsRequest(mFriendsAdapter, mFriendsArray), mOffset, mStep);
                    mFriendsAdapter.setIsUpdatedData(false);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Bundle bundle = new Bundle();
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserId(String.valueOf(((VKApiUserFull)l.getItemAtPosition(position)).id));
        //bundle.putString(FriendsFragment.id, Integer.toString(((VKApiUserFull) l.getItemAtPosition(position)).id));
        //chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    public static class VKFriendsRequest extends VKRequest.VKRequestListener {
        private List<VKApiUserFull> mFriendsArray;
        private FriendsAdapter mFriendsAdapter;

        public VKFriendsRequest(ArrayAdapter friendsAdapter, List<VKApiUserFull> friendsArray) {
            this.mFriendsArray = friendsArray;
            this.mFriendsAdapter = (FriendsAdapter) friendsAdapter;
        }

        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            mFriendsAdapter.setCountOfAllUsers(((VKUsersArray) response.parsedModel).getCount());
            for (VKApiUserFull currentUser : (VKUsersArray) response.parsedModel) {
                mFriendsArray.add(currentUser);
            }
            mFriendsAdapter.setIsUpdatedData(true);
            mFriendsAdapter.notifyDataSetChanged();
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            super.attemptFailed(request, attemptNumber, totalAttempts);
        }

        @Override
        public void onError(VKError error) {
            super.onError(error);
        }

        @Override
        public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
            super.onProgress(progressType, bytesLoaded, bytesTotal);
        }
    }
}
