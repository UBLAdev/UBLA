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

public class ConvertOriginAsyncTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "ConvertOriginAsyncTask";

    private OnAdressConverted listener;
    private String address;

    public ConvertOriginAsyncTask(OnAdressConverted listener, String address) {
        this.listener = listener;
        this.address = address;
    }

    @Override
    protected Void doInBackground(String... strings) {

        MapboxGeocoding originMapboxGeocoding;

        // set up request
        originMapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA")
                .query(address)
                .build();

        // enqueue request
        originMapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                // check for results
                if (results.size() > 0) {
                    // Log first result Point
                    Point firstResultPoint = results.get(0).center();

                    // trigger callback
                    listener.onOriginConverted(firstResultPoint);
                    Log.i(TAG, "onResponse returned " + firstResultPoint.toString());
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
