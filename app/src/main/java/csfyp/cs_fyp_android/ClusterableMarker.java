package csfyp.cs_fyp_android;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterableMarker implements ClusterItem {

    private final LatLng mPosition;
    private BitmapDescriptor icon;
    private String title;
    private String snippet;

    public BitmapDescriptor getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public ClusterableMarker(BitmapDescriptor icon, Double lat , Double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        this.icon = icon;
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
