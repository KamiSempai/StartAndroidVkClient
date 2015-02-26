package ru.startandroid.vkclient.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

import ru.startandroid.vkclient.GeneralFriendsFields;
import ru.startandroid.vkclient.R;
import ru.startandroid.vkclient.adapters.FriendsAdapter;
import ru.startandroid.vkclient.gcm.LongPollService;
import ru.startandroid.vkclient.requests.FriendsRequest;

/**
 * @author Samofal Vitaliy
 *         Фрагмент отвечающий за сбор информации и отображение друзей.
 */

public class FriendsFragment extends ListFragment {

    public static final String id = "ID";

//    private List<VKApiUserFull> mFriendsArray = new ArrayList<>(20);
    private SparseArray<VKApiUserFull> mFriendsArray = new SparseArray<>(20);
//    private FriendsAdapter mFriendsAdapter;
    private FriendsAdapter mFriendsAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    private int mOffset;
    private int mStep;
    private Boolean mIsAllUsersDownloaded = false;
    private int mQuantityOfAllUser = 0;


    public FriendsFragment(){
        this(0,20);
    }
    public FriendsFragment(int offset,int step){
        mOffset = offset;
        mStep = step;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(LongPollService.USER_ONLINE_LP_ACTION)) {
                    mFriendsAdapter.setOnline(Integer.parseInt(intent.getStringExtra(LongPollService.USER_ONLINE_USER_ID_KEY)),true);
                }
                if(intent.getAction().equals(LongPollService.USER_OFFLINE_LP_ACTION)){
                    mFriendsAdapter.setOnline(Integer.parseInt(intent.getStringExtra(LongPollService.USER_OFFLINE_USER_ID_KEY)),false);
                }


            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LongPollService.USER_ONLINE_LP_ACTION);
        intentFilter.addAction(LongPollService.USER_OFFLINE_LP_ACTION);

        getActivity().registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFriendsAdapter = new FriendsAdapter(getActivity(), mFriendsArray, R.layout.friends_list);

        final FriendsRequest friendsRequest = new FriendsRequest(GeneralFriendsFields.FIRSTNAME_LASTNAME_ICON_ONLINE, mOffset, mStep);
        friendsRequest.executeWithListener(new VKFriendsRequestListener());
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
                        (firstVisibleItem + visibleItemCount) == totalItemCount) {
                    int diffOfCount = (mQuantityOfAllUser - (mOffset + mStep));
                    mStep = diffOfCount < 20 ? diffOfCount : mStep;
                    mIsAllUsersDownloaded = diffOfCount <= 0;
                    mOffset += 20;
                    friendsRequest.executeWithListener(new VKFriendsRequestListener(), mOffset, mStep);
                    mFriendsAdapter.setUpdatedData(false);

                }

            }
        });
        }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle bundle = new Bundle();
        ChatFragment chatFragment = new ChatFragment();
        bundle.putString(FriendsFragment.id, Integer.toString(((VKApiUserFull) l.getItemAtPosition(position)).id));
        chatFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    public class VKFriendsRequestListener extends VKRequest.VKRequestListener {


        public VKFriendsRequestListener() {
            super();
        }

        @Override
        public void onComplete(VKResponse response) {
            super.onComplete(response);
            mQuantityOfAllUser = ((VKUsersArray) response.parsedModel).getCount();
            for (VKApiUserFull currentUser : (VKUsersArray) response.parsedModel) {
                mFriendsAdapter.addItem(currentUser);
            }
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
