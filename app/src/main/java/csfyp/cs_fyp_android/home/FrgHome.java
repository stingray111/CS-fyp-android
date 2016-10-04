package csfyp.cs_fyp_android.home;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import csfyp.cs_fyp_android.R;

public class FrgHome extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";
    private boolean mIsPanelExpanded;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;
    private SlidingUpPanelLayout mLayout;


    public FrgHome() {
    }

    public static FrgHome newInstance() {

        Bundle args = new Bundle();

        FrgHome fragment = new FrgHome();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            mIsPanelExpanded = savedInstanceState.getBoolean("isPanelExpanded");
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPanelExpanded", mIsPanelExpanded);
        mMapView.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_frg, container, false);

        // Gets the toolbar from XML
        Toolbar myToolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);

        // Gets the MapView from XML
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

//        // Gets to GoogleMap from the MapView and does initialization stuff
//        mGoogleMap = mMapView.getMap();
//        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mGoogleMap.setMyLocationEnabled(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.5f);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Fragment newFragment = FrgExpandPanelAppBar.newInstance();
                Log.i(TAG, previousState + ":" + newState);
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    mIsPanelExpanded = true;
                else
                    mIsPanelExpanded = false;

                if(mIsPanelExpanded) {
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    Log.i(TAG, "add back");
                    ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_top_exit);
                    ft.add(R.id.frg_container, newFragment);
                    ft.commit();
                } else {
                    FragmentManager fm = getChildFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    FrgExpandPanelAppBar f = (FrgExpandPanelAppBar) fm.findFragmentById(R.id.frg_container);
                    if(f != null){
                        Log.i(TAG, "remove back");
                        ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_top_exit);
                        ft.remove(f);
                        ft.commit();
                    }
                }
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng hk = new LatLng(22.25, 114.1667);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions().position(hk).title("Marker in Hong Kong"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(hk));
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
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
