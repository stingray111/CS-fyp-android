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
import csfyp.cs_fyp_android.databinding.PassedEventFrgBinding;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.RefreshFrg;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.Participation;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventRequest;
import csfyp.cs_fyp_android.model.request.Rate;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.rating.FrgRating;
import retrofit2.Call;
import retrofit2.Response;

public class FrgPassedEvent extends CustomFragment implements OnMapReadyCallback,LoaderManager.LoaderCallbacks<Event> {
    public FrgPassedEvent() {}
    public static final int EVENT_LOADER_ID  = 2;
    public static final String TAG = "PassedEventFragment";
    private PassedEventFrgBinding mDataBinding;
    private boolean mIsSelfHold = false;
    private boolean mIsAttendence = false;
    private boolean mIsMapReady = false;
    private boolean mIsLoaded = false;

    private Event mEventObj;
    private int mSelfUserId;

    private Toolbar mToolBar;

    private GoogleMap mGoogleMap;
    private GoogleApiClient client;
    private MapView mMapView;

    private int mEventId;
    private List<User> mUserList;
    private RecyclerView mUserRecyclerView;
    private RecyclerView mAttandanceRecyclerView;
    private AdtUser mUserAdapter;
    private AdtAttendanceUser mAttendanceUserAdapter;
    private RecyclerView.LayoutManager mUserLayoutManager;
    private RecyclerView.LayoutManager mAttendanceLayoutManager;

    private Response<Event> mEventRespond;

    public static FrgPassedEvent newInstance(int id) {
        FrgPassedEvent fragment = new FrgPassedEvent();

        Bundle args = new Bundle();
        args.putInt("eventId", id);
        fragment.setArguments(args);
        return fragment;
    }

    private void resetLoader() {
        Log.i(TAG, "get event again");
        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, this);
    }

    public int getmEventId() {
        return mEventId;
    }

    public Event getmEventObj() {
        return mEventObj;
    }

    public int getmSelfUserId() {
        return mSelfUserId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSelfHold", mIsSelfHold);
        outState.putBoolean("isAttendence", mIsAttendence);
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
            mIsAttendence = savedInstanceState.getBoolean("isAttendence");
            mIsMapReady = savedInstanceState.getBoolean("isMapReady");
            mIsLoaded = savedInstanceState.getBoolean("isLoaded");
        }

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.passed_event_frg, container,false);
        mDataBinding.setHandlers(this);
        View v  = mDataBinding.getRoot();

        // Setting up RecyclerView for event
        mUserLayoutManager = new LinearLayoutManager(getContext());
        mAttendanceLayoutManager = new LinearLayoutManager(getContext());
        mUserRecyclerView = mDataBinding.rvUser;
        mUserRecyclerView.setLayoutManager(mUserLayoutManager);
        mUserAdapter = new AdtUser(this);
        mUserRecyclerView.setAdapter(mUserAdapter);

        // Setting up RecyclerView for attendace
        mAttandanceRecyclerView = mDataBinding.rvAttendaceUser;
        mAttandanceRecyclerView.setLayoutManager(mAttendanceLayoutManager);
        mAttendanceUserAdapter = new AdtAttendanceUser(this);
        mAttandanceRecyclerView.setAdapter(mAttendanceUserAdapter);

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

        getLoaderManager().initLoader(EVENT_LOADER_ID, null, this);

        mSelfUserId = ((MainActivity)getActivity()).getmUserId();

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
            mDataBinding.passedEventProgressBar.setVisibility(View.GONE);

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
                .setName("Main Page") // TODO: Define a title for the content shown.
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

        if(mEventObj != null){
            int selfId = ((MainActivity)getActivity()).getmUserId();

            if (selfId == data.getHolderId()) {
                mIsSelfHold = true;
            }

            if (mIsSelfHold) {
                mDataBinding.holderRateBtn.setVisibility(View.INVISIBLE);
                mDataBinding.checkAttendance.setVisibility(View.VISIBLE);

            } else {
                mDataBinding.checkAttendance.setVisibility(View.GONE);

                // check if self attend the event
                if (mEventObj.getAttendace() != null) {
                    for (Participation participation: mEventObj.getAttendace()) {
                        if (participation.getUserId() == ((MainActivity)getActivity()).getmUserId()) {
                            if (!participation.isAttended())
                                mDataBinding.notAttendedMsg.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            // set the attendance of the users
            if (mEventObj.getAttendace() != null) {
                for (Participation participation: mEventObj.getAttendace()) {
                    Log.i("hihi", participation.getUserId() + " " + participation.isAttended());

                    if (participation.isAttended()) {
                        for (User user: mEventObj.getParticipantList()) {
                            if (participation.getUserId() == user.getId()) {
                                user.setAttended(true);
                            }
                        }
                    }
                }
            }

            // check users if rated
            if (mEventObj.getRates() != null) {
                for (Rate rate: mEventObj.getRates()) {
                    if (mEventObj.getHolder().getId() == rate.getOtherUserId()) {
                        mDataBinding.holderRateBtn.setVisibility(View.INVISIBLE);
                        mDataBinding.holderRatedImg.setVisibility(View.VISIBLE);
                    }
                    if (((MainActivity)getActivity()).getmSelf().isSelfRated()) {
                        for (User user: mEventObj.getParticipantList()) {
                            if (rate.getOtherUserId() == user.getId())
                                user.setRatedbyOther(true);
                        }
                    } else {
                        mDataBinding.notSelfRatedMsg.setVisibility(View.VISIBLE);
                    }
                }
            } // TODO: 9/2/2017 performance?
            
            mUserAdapter.setmUserList(mEventObj.getParticipantList());
            mUserAdapter.notifyDataSetChanged();

            mAttendanceUserAdapter.setmUserList(mEventObj.getParticipantList());
            mAttendanceUserAdapter.notifyDataSetChanged();

            mDataBinding.setEventObj(mEventObj);

            if (mIsMapReady)
                mDataBinding.passedEventProgressBar.setVisibility(View.GONE);

            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_self_marker)));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()), 12.0f));


//            if (mIsAttendence) {
//
//            } else {
//
//            }
            
            //// TODO: 8/2/2017 fix attendance problem 
        }
    }

    @Override
    public void onLoaderReset(Loader<Event> loader) {

    }
    
    //// TODO: 23/1/2017 change this 
    public void onClickHolder(View v) {
        switchFragment(this, FrgProfile.newInstance(mEventObj.getHolder().getId()));
    }

    public void onClickHolderRate(View v) {
        switchFragment(this, FrgRating.newInstance(mEventObj.getHolder().getId(), mEventObj.getHolder().getUserName(), mEventId));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshFrg event) {
        Log.i("EventBus", "Received");
        if (event.getTag() == TAG) {
            getLoaderManager().restartLoader(EVENT_LOADER_ID, null, this);
            mUserAdapter.notifyDataSetChanged();
        }
    }
}
