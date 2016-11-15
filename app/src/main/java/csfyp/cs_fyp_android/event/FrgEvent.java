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

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EventFrgBinding;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventId;
import retrofit2.Call;
import retrofit2.Response;


public class FrgEvent extends CustomFragment implements OnMapReadyCallback,LoaderManager.LoaderCallbacks<Event>{
    public FrgEvent() {
    }
    private static final int EVENT_LOADER_ID  = 2;
    private static final String TAG = "EventFragment";
    private EventFrgBinding mDataBinding;
    private Event mEventObj;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        mEventId = args.getInt("eventId");

        mDataBinding = DataBindingUtil.inflate(inflater,R.layout.event_frg, container,false);
        mDataBinding.setHandlers(this);
        View v  = mDataBinding.getRoot();

        // Setting up RcyclerView for event
        mUserRecyclerView = mDataBinding.rvUser;
        mUserLayoutManager = new LinearLayoutManager(getContext());
        mUserRecyclerView.setLayoutManager(mUserLayoutManager);
        mUserAdapter = new AdtUser();
        mUserRecyclerView.setAdapter(mUserAdapter);

        //Tool Bar
        mToolBar = mDataBinding.eventToolBar;
        mToolBar.setTitle("username");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mMapView = mDataBinding.eventMap;
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);

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

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
                Call<Event> call = httpService.getEvent(new EventId(mEventId));
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
        mEventObj = data;
        if(mEventObj != null){
            mUserAdapter.setmUserList(mEventObj.getParticipantList());
            mUserAdapter.notifyDataSetChanged();

            mDataBinding.setEventObj(mEventObj);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()), 12.0f));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mEventObj.getLatitude(), mEventObj.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_self_marker)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Event> loader) {

    }
}

