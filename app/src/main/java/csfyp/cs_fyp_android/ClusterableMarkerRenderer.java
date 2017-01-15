package csfyp.cs_fyp_android;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class ClusterableMarkerRenderer extends DefaultClusterRenderer<ClusterableMarker> {
    public ClusterableMarkerRenderer(Context context, GoogleMap map,
                       ClusterManager<ClusterableMarker> clusterManager) {
        super(context, map, clusterManager);
    }


    protected void onBeforeClusterItemRendered(ClusterableMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
