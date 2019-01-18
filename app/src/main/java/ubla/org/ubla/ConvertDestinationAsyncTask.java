package ubla.org.ubla;

import android.os.AsyncTask;
import android.util.Log;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertDestinationAsyncTask extends AsyncTask<Point, Void, Void> {

    private final static String TAG = "ConvertDestinationAsyncTask";

    private OnAdressConverted listener;
    private String destination;

    public ConvertDestinationAsyncTask(OnAdressConverted listener, String destination) {
        this.listener = listener;
        this.destination = destination;
    }

    @Override
    protected Void doInBackground(Point... points) {

        MapboxGeocoding destinationMapboxGeocoding;

        // set up request
        destinationMapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA")
                .query(destination)
                .build();

        // TODO: refactor Callback!
        // enqueue request
        destinationMapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();

                // check for results
                if (results.size() > 0) {
                    // Log first result Point
                    Point firstResultPoint = results.get(0).center();

                    // trigger callback
                    listener.onDestinationConverted(firstResultPoint);
                    Log.i(TAG, "onresponse returned " + firstResultPoint.toString());
                } else {
                    Log.e(TAG, "no results were found.");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                // print stack trace
                t.printStackTrace();
            }
        });

        return null;
    }
}
