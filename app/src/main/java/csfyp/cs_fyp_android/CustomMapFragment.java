package csfyp.cs_fyp_android;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import static android.app.Activity.RESULT_OK;
import static csfyp.cs_fyp_android.home.FrgHome.HOME_LOCATION_SETTING_CALLBACK;
import static csfyp.cs_fyp_android.home.FrgHome.HOME_PERMISSION_CALLBACK;

public class CustomMapFragment extends CustomFragment implements OnMapReadyCallback,
        ResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        LocationListener {


    protected GoogleMap mGoogleMap;
    protected MapView mMapView;
    private GoogleApiClient client;

    protected LatLng mLastTarget;
    protected float mLastZoom;

    // location
    protected Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private boolean mIsSetToInitLocation = false;

    // permission & setting of location service
    private boolean mIsPermissionGranted = false;
    private boolean mIsLocationSettingEnable = false;

    // map loading status
    protected boolean mIsMapReady = false;

    // location setting
    private boolean isUseLocationService = false;

    // map ui setting
    protected boolean isZoomControlsEnabled = true; // zoom button
    protected boolean isMyLocationButtonEnabled = true; // my location button
    protected boolean isAllGesturesEnabled = true; // all gesture
    protected boolean isMapToolbarEnabled = false; // tool bar

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastTarget = savedInstanceState.getParcelable("lastTarget");
        mLastZoom = savedInstanceState.getFloat("lastZoom");
        mIsSetToInitLocation = savedInstanceState.getBoolean("isSetToInitLocation");

        // check location setting
        if (isUseLocationService)
            buildGoogleApiClient();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (savedInstanceState != null) {
            Bundle mapState;
            mapState = savedInstanceState.getBundle("mapSaveInstanceState"); // TODO: 19/10/2016 change key
            mMapView.onCreate(mapState);
        }
        else
            mMapView.onCreate(null);

        mMapView.getMapAsync(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("lastTarget", mLastTarget);
        outState.putFloat("lastZoom", mLastZoom);
        outState.putBoolean("isSetToInitLocation", mIsSetToInitLocation);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null)
            mMapView.onResume();
        if (isUseLocationService && client.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null)
            mMapView.onPause();
        if (isUseLocationService && client.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null)
            mMapView.onLowMemory();
    }

    public void switchFragment(Fragment fragment) {
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
        super.switchFragment(this, fragment);
    }

    // map related
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mIsMapReady = true;
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(isZoomControlsEnabled);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(isMyLocationButtonEnabled);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(isAllGesturesEnabled);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(isMapToolbarEnabled);

        mGoogleMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        // move the camera back to the last state
        if (mLastTarget != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastTarget, mLastZoom));


    }

    // method for info window
    @Override
    public void onInfoWindowClick(Marker marker) {
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
    }

    // method for marker dragging
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    //method for location

    private void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        LocationServices.SettingsApi.checkLocationSettings(client, mLocationSettingsRequest)
                .setResultCallback(this);
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mIsLocationSettingEnable && mIsPermissionGranted) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (!mIsSetToInitLocation) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 11.0f));
            mIsSetToInitLocation = true;
        }
    }

    // api client connection
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
    public void onConnected(@Nullable Bundle bundle) {
        // check location setting

        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        // check permission for location
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, HOME_PERMISSION_CALLBACK);
            }
        } else {
            // permission granted
            mIsPermissionGranted = true;
            startLocationUpdate();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // permission result callback
    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                mIsLocationSettingEnable = true;
                startLocationUpdate();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), HOME_LOCATION_SETTING_CALLBACK);
                } catch (IntentSender.SendIntentException e) {
                    // TODO: 27/10/2016 ignore?
                    Toast.makeText(getContext(),"Try get Location fail", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HOME_PERMISSION_CALLBACK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIsPermissionGranted = true;
                startLocationUpdate();
            }
        }
    }

    // setting result callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // result for location setting prompt
        if (requestCode == HOME_LOCATION_SETTING_CALLBACK) {
            if (resultCode == RESULT_OK) {
                mIsLocationSettingEnable = true;
                startLocationUpdate();
            } else {
                LocationServices.SettingsApi.checkLocationSettings(client, mLocationSettingsRequest)
                        .setResultCallback(this);
            }
        }
    }
}
