package csfyp.cs_fyp_android.event;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EventFrgBinding;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.RefreshLoader;
import csfyp.cs_fyp_android.lib.eventBus.SwitchFrg;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventJoinQuitRequest;
import csfyp.cs_fyp_android.model.request.EventRequest;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.profile.FrgProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static csfyp.cs_fyp_android.currentEvent.FrgCurrentEvent.CURRENT_EVENT_LOADER_ID;


public class FrgEvent extends CustomFragment implements OnMapReadyCallback,LoaderManager.LoaderCallbacks<Event>{
    public FrgEvent() {}
    private static final int EVENT_LOADER_ID  = 2;
    public static final String TAG = "EventFragment";
    private EventFrgBinding mDataBinding;
    private Event mEventObj;
    private boolean mIsSelfHold = false;
    private boolean mIsJoined = false;
    private boolean mIsMapReady = false;
    private boolean mIsLoaded = false;

    private Toolbar mToolBar;

    private GoogleMap mGoogleMap;
    private GoogleApiClient client;
    private MapView mMapView;

    private int mEventId;
    private List<User> mUserList;
    private RecyclerView mUserRecyclerView;
    private AdtUser mUserAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;

    private Response<Event> mEventRespond;

    public static FrgEvent newInstance(int id) {
        FrgEvent fragment = new FrgEvent();

        Bundle args = new Bundle();
        args.putInt("eventId", id);
        fragment.setArguments(args);
        return fragment;
    }

    private void resetLoader() {
        Log.i(TAG, "refresh");
        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, this);
    }

    private void showJoin() {
        mDataBinding.joinQuitProgressBar.setVisibility(View.GONE);
        mDataBinding.deleteEvent.setVisibility(View.GONE);
        mDataBinding.joinEvent.setVisibility(View.VISIBLE);
        mDataBinding.quitEvent.setVisibility(View.GONE);
    }

    private void showQuit() {
        mDataBinding.joinQuitProgressBar.setVisibility(View.GONE);
        mDataBinding.deleteEvent.setVisibility(View.GONE);
        mDataBinding.joinEvent.setVisibility(View.GONE);
        mDataBinding.quitEvent.setVisibility(View.VISIBLE);
    }

    private void showDelete() {
        mDataBinding.joinQuitProgressBar.setVisibility(View.GONE);
        mDataBinding.deleteEvent.setVisibility(View.VISIBLE);
        mDataBinding.joinEvent.setVisibility(View.GONE);
        mDataBinding.quitEvent.setVisibility(View.GONE);
    }

    private void showProgress() {
        mDataBinding.joinQuitProgressBar.setVisibility(View.VISIBLE);
        mDataBinding.deleteEvent.setVisibility(View.GONE);
        mDataBinding.joinEvent.setVisibility(View.GONE);
        mDataBinding.quitEvent.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSelfHold", mIsSelfHold);
        outState.putBoolean("isJoined", mIsJoined);
        outState.putBoolean("isMapReady", mIsMapReady);
        outState.putBoolean("isLoaded", mIsLoaded);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        mEventId = args.getInt("eventId");

        if (savedInstanceState != null) {
            mIsSelfHold = savedInstanceState.getBoolean("isSelfHold");
            mIsJoined = savedInstanceState.getBoolean("isJoined");
            mIsMapReady = savedInstanceState.getBoolean("isMapReady");
            mIsLoaded = savedInstanceState.getBoolean("isLoaded");
        }

        mDataBinding = DataBindingUtil.inflate(inflater,R.layout.event_frg, container,false);
        mDataBinding.setHandlers(this);
        View v  = mDataBinding.getRoot();

        // Setting up RcyclerView for event
        mUserRecyclerView = mDataBinding.rvUser;
        mUserLayoutManager = new LinearLayoutManager(getContext());
        mUserRecyclerView.setLayoutManager(mUserLayoutManager);
        mUserAdapter = new AdtUser(AdtUser.NORMAL_MODE, mEventId);
        mUserRecyclerView.setAdapter(mUserAdapter);

        // Tool Bar
        mToolBar = mDataBinding.eventToolBar;
        mToolBar.setTitle("Event");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        // Google map
        mMapView = mDataBinding.eventMap;
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);

        // join button
        mDataBinding.joinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<ErrorMsgOnly> call = httpService.joinEvent(new EventJoinQuitRequest(mEventId, ((MainActivity) getActivity()).getmUserId()));
                showProgress();
                call.enqueue(new Callback<ErrorMsgOnly>() {
                    @Override
                    public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                        Log.i(TAG, "responese here");
                        if (response.isSuccessful()) {
                            Log.i(TAG, "is 200");
                            if (response.body().getErrorMsg() == null) {
                                Toast.makeText(getContext(), "Joined successfully", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Joined successfully");
                                mIsJoined = true;
                                EventBus.getDefault().post(new RefreshLoader(CURRENT_EVENT_LOADER_ID));
                                resetLoader();
                                showQuit();

                                if(!((MainActivity)getActivity()).mChatService.addEvent(mEventObj)){
                                    Log.d(TAG,"messager service return false");
                                }
                            }
                            else
                                Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        } else
                            Log.i(TAG, "is not 200");
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                        Log.i(TAG, "err" + t.getMessage());
                        Toast.makeText(getContext(), "Cannot join event: unknown err", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        mDataBinding.quitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<ErrorMsgOnly> call = httpService.quitEvent(new EventJoinQuitRequest(mEventId, ((MainActivity) getActivity()).getmUserId()));
                mDataBinding.joinQuitProgressBar.setVisibility(View.VISIBLE);
                mDataBinding.quitEvent.setVisibility(View.GONE);
                call.enqueue(new Callback<ErrorMsgOnly>() {
                    @Override
                    public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getErrorMsg() == null) {
                                Toast.makeText(getContext(), "Quited successfully", Toast.LENGTH_SHORT).show();
                                mIsJoined = false;
                                EventBus.getDefault().post(new RefreshLoader(CURRENT_EVENT_LOADER_ID));
                                resetLoader();
                                showJoin();

                                if(!((MainActivity)getActivity()).mChatService.dropEvent(mEventId)){
                                    Log.d(TAG,"Chat Service Return false");
                                }
                            }
                            else
                                Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                        Toast.makeText(getContext(), "Cannot quit event: unknown err", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        mDataBinding.deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<ErrorMsgOnly> call = httpService.deleteEvent(new EventJoinQuitRequest(mEventId, ((MainActivity) getActivity()).getmUserId()));
                call.enqueue(new Callback<ErrorMsgOnly>() {
                    @Override
                    public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getErrorMsg() == null) {
                                Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                //TODO: drop the messaging record
                                if(!((MainActivity)getActivity()).mChatService.dropEvent(mEventId)){
                                    Log.d(TAG,"Chat Service return false");
                                }
                                EventBus.getDefault().post(new RefreshLoader(CURRENT_EVENT_LOADER_ID));
                                onBack(null);
                            }
                            else
                                Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {

                    }
                });
            }
        });

        if (mIsSelfHold) {
            showDelete();
        }

        if (mIsJoined) {
            showQuit();
        } else {
            showJoin();
        }

        getLoaderManager().initLoader(EVENT_LOADER_ID, null, this);

        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        mIsMapReady = true;

        if (mIsLoaded)
            mDataBinding.eventProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Event") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public Loader<Event> onCreateLoader(int id, Bundle args) {

        return new CustomLoader<Event>(getContext()) {
            @Override
            public Event loadInBackground() {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<Event> call = httpService.getEvent(new EventRequest(mEventId, ((MainActivity)getActivity()).getmUserId()));
                try {
                    mEventRespond = call.execute();
                    if(mEventRespond.isSuccessful()){
                        Log.i(TAG, "Event load Success");
                        return mEventRespond.body();
                    } else
                        return null;
                } catch (Exception e) {
                    return null;
                    // todo excpetion handling
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Event> loader, Event data) {
        mIsLoaded = true;

        mEventObj = data;

        if (mEventObj != null) {
            mUserAdapter.setmUserList(mEventObj.getParticipantList());
            mUserAdapter.notifyDataSetChanged();

            mDataBinding.setEventObj(mEventObj);
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_self_marker)));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()), 12.0f));

            if (mIsMapReady) {
                mDataBinding.eventProgressBar.setVisibility(View.GONE);
            }

            int selfId = ((MainActivity)getActivity()).getmUserId();

            if (selfId == data.getHolderId()){
                mIsSelfHold = true;
                showDelete();
                return;
            }

            if (mIsJoined) {
                showQuit();
                return;
            }

            for (User item: data.getParticipantList()) {
                if(item.getId() == selfId)
                    mIsJoined = true;
            }

            if (mIsJoined) {
                showQuit();
            } else {
                showJoin();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Event> loader) {

    }

    public void onClickHolder(View v) {
        switchFragment(this, FrgProfile.newInstance(mEventObj.getHolder().getId()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SwitchFrg event) {
        if (event.getFromTag().equals(TAG) && event.getToTag().equals(FrgProfile.TAG)) {
            switchFragment(this, FrgProfile.newInstance(event.getBundle().getInt("userId")));
        }
    }

}

