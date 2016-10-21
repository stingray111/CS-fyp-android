package csfyp.cs_fyp_android.event;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EventFrgBinding;


public class FrgEvent extends CustomFragment implements OnMapReadyCallback {
    public FrgEvent() {
        super();
    }
    private static final String TAG = "EventFragment";
    private Toolbar mToolBar;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;
    private MapView mMapView;
    private EventFrgBinding mDataBinding;

    public static FrgEvent newInstance() {
        
        Bundle args = new Bundle();
        
        FrgEvent fragment = new FrgEvent();
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
        mDataBinding = DataBindingUtil.inflate(inflater,R.layout.event_frg, container,false);
        mDataBinding.setHandlers(this);
        View v  = mDataBinding.getRoot();

        //Tool Bar
        mToolBar = (Toolbar) v.findViewById(R.id.eventToolBar);
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

        mMapView = (MapView) v.findViewById(R.id.eventMap);
        mMapView.onCreate(null);
        mMapView.getMapAsync(this);


        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap  = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        //TODO: disable zoom by gesture

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(22.25, 114.1667))
                .title("Event 1")
                .snippet("Custom")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(22.28, 114.1679))
                .title("Event 2")
                .snippet("Custom2")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.25, 114.1667), 12.0f));
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

}

