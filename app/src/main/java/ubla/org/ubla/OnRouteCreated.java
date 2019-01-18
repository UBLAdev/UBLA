package ubla.org.ubla;

import com.mapbox.api.directions.v5.models.DirectionsRoute;

public interface OnRouteCreated {
    public void onRouteCreated(DirectionsRoute directionsRoute);
}
