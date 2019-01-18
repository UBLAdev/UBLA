package ubla.org.ubla;

import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRouteAsyncTask extends AsyncTask<Point, Void, Void> {

    private static final String TAG = "CreateRouteAsyncTask";

    private OnRouteCreated listener;
    private Point origin;
    private Point destination;

    public CreateRouteAsyncTask(OnRouteCreated listener, Point origin, Point destination) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        Log.i(TAG, "constructor returned.");
    }

    @Override
    protected Void doInBackground(Point... points) {

        Log.i(TAG, origin.toString() + " : " + destination.toString());

        // build Directions request
        MapboxDirections mapboxDirectionsClient = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken("pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA")
                .build();

        // send request and handling response
        mapboxDirectionsClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.e(TAG, "no routes found, something is wrong (response.body == null).");
                } else if (response.body().routes().size() < 1) {
                    Log.e(TAG, "no routes found.");
                }

                Log.i(TAG, response.body().routes().get(0).toString());

                // call back to MainActivity
                listener.onRouteCreated(response.body().routes().get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return null;
    }
}
