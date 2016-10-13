package csfyp.cs_fyp_android.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.HomeFrgBinding;

public class FrgHome extends CustomFragment implements OnMapReadyCallback {

    private static final String TAG = "HomeFragment";
    private boolean mIsPanelExpanded;
    private boolean mIsPanelAnchored;
    private HomeFrgBinding mDataBinding;

    // For Toolbar
    private Toolbar mToolBar;

    // For Event Recycler View
    private RecyclerView mEventRecyclerView;
    private RecyclerView.Adapter mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;

    // For Left Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView mDrawerRecyclerView;
    private RecyclerView.Adapter mDrawerAdapter;
    private RecyclerView.LayoutManager mDrawerLayoutManager;

    // For Google Map
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;

    // For Sliding Up Panel
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

        // initialize Pull-up Panel, try getting the last status
        if (savedInstanceState != null)
            mIsPanelExpanded = savedInstanceState.getBoolean("isPanelExpanded");

        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        outState.putBundle("mapSaveInstanceState", mapState);
        outState.putBoolean("isPanelExpanded", mIsPanelExpanded);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDataBinding = DataBindingUtil.inflate(
                inflater, R.layout.home_frg, container, false);
        View v = mDataBinding.getRoot();
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

        // Setting up Action Bar
        mToolBar = (Toolbar) v.findViewById(R.id.homeToolbar);
        mToolBar.setTitle("Home");
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_hamburger);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Setting up Pull-up Panel
        if (mIsPanelExpanded) {
            Fragment expandPanelAppBar = FrgExpandPanelAppBar.newInstance();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.frg_container, expandPanelAppBar);
            ft.commit();
        } else {
            Fragment notExpandPanelAppBar = FrgNotExpandPanelAppBar.newInstance();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.frg_container, notExpandPanelAppBar);
            ft.commit();
        }

        // Setting up Navigation Drawer
        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        // Setting up RecyclerView for Navigation bar
        mDrawerRecyclerView = (RecyclerView) v.findViewById(R.id.rvDrawer);
        mDrawerLayoutManager = new LinearLayoutManager(getContext());
        mDrawerRecyclerView.setLayoutManager(mDrawerLayoutManager);
        ArrayList<String> data = new ArrayList<>();
        data.add("Joined Events");
        data.add("History");
        data.add("About Us");
        data.add("Setting");
        mDrawerAdapter = new AdtNavigationItem(data);
        mDrawerRecyclerView.setAdapter(mDrawerAdapter);

        // Setting up Google Map
        mMapView = (MapView) v.findViewById(R.id.map);
        Bundle mapState;
        if (savedInstanceState != null)
            mapState = savedInstanceState.getBundle("mapSaveInstanceState");
        else
            mapState = null;
        mMapView.onCreate(mapState);
        mMapView.getMapAsync(this);

        // Setting up RcyclerView for event
        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.rvEvent);
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtHome();
        mEventRecyclerView.setAdapter(mEventAdapter);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting up listener for Pull-up Panel
        mLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, previousState + ":" + newState);

                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    mIsPanelExpanded = true;
                else
                    mIsPanelExpanded = false;

                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED)
                    mIsPanelAnchored = true;
                else
                    mIsPanelAnchored = false;

                if (mIsPanelExpanded) {
                    Fragment expandPanelAppBar = FrgExpandPanelAppBar.newInstance();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.frg_container, expandPanelAppBar);
                    ft.commit();
                } else {
                    Fragment notExpandPanelAppBar = FrgNotExpandPanelAppBar.newInstance();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.frg_container, notExpandPanelAppBar);
                    ft.commit();
                }
            }
        });
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mGoogleMap.setMyLocationEnabled(true);
            return;
        }

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View myContentView = LayoutInflater.from(getContext()).inflate(
                        R.layout.home_map_item_info_window, null);
                TextView tvInfoWinTitle = ((TextView) myContentView
                        .findViewById(R.id.infoWinTitle));
                tvInfoWinTitle.setText(marker.getTitle());
                return myContentView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

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

    public void onClickProfile(View v) {
        switchFragment(FrgProfile.newInstance());
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


