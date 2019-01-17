package ubla.org.ubla;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, Style.OnStyleLoaded {

    // Constants
    private static final String TAG = "MainActivity";
    // TODO: customize?!?
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";

    // MapView Object
    private MapView mapView;

    // MapboxMap stuff
    private MapboxMap mapboxMap;

    // LocationComponent stuff
    private LocationComponent locationComponent;

    // Navigation stuff
    private MapboxNavigation mapboxNavigation;
    private Point origin;
    private Point destination;
    private DirectionsRoute currentRoute;
    private Feature directionsRouteFeature;
    private Boolean isOriginSet = false;
    private Boolean isDestinationSet = false;

    // Permission Stuff
    private PermissionsManager permissionsManager;

    // UI elements
    private Button startRouteButton;
    private EditText originEditText;
    private EditText destinationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // handle Permissions
        getPermissions();

        // get Instance of MapView
        // TODO: Refactor hardcoded API key!!
        Mapbox.getInstance(this, "pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA");
        setContentView(R.layout.activity_main);

        // get MapView and prepare it for use
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // customize the map for Routes
        initSource();
        initLayer();

        // set up UI elements
        startRouteButton = (Button) findViewById(R.id.startRouteButton);
        originEditText = (EditText) findViewById(R.id.originEditText);
        destinationEditText = (EditText) findViewById(R.id.destinationEditText);
    }

    // ---------- Handle Permission Stuff ---------- //

    private void getPermissions() {
        permissionsManager = new PermissionsManager(this);
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            return;
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    //// ---------- PermissionListener Callbacks implementation ---------- ////

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            Log.i(TAG, "User has granted Permissions.");
        } else {
            Log.e(TAG, "User has not granted permissions.");
        }
    }

    // TODO: Is this necessary??
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // ---------- LocationComponent stuff ---------- //

    // must only be called if permission is already granted and checked!
    @SuppressLint("MissingPermission")
    @SuppressWarnings( {"Missing Permission"} ) // due to permission handling with Mapbox Core util
    private void initLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationComponent = mapboxMap.getLocationComponent();

            //TODO: Options can be set here
            // Activate LocationComponent without setting options
            locationComponent.activateLocationComponent(this, mapboxMap.getStyle());

            // Enable to make visible
            locationComponent.setLocationComponentEnabled(true);

            // Set Camera Mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set Render Mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    // TODO: What else has to be set?
    public void initMapboxMap() {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, (Style.OnStyleLoaded) this);
        Log.i(TAG, "initMapBoxMap() has finished.");
        // Log.i(TAG, mapboxMap.getStyle().toString());
    }

    //// ---------- OnMapReadyCallback implementation ---------- //

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        initMapboxMap();
    }

    //// ---------- OnStyleLoaded implementation ---------- //

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        Log.i(TAG, "entered onStyleLoaded");
        initLocationComponent();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        // Register UI Callbacks
        startRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originEditText.getText().length() > 3 && destinationEditText.getText().length() > 3) {
                    showRoute();
                } else {
                    Log.e(TAG, "input too short, must be min 4 chard each.");
                }
            }
        });
    }

    // ---------- Route Stuff ---------- //

    private void showRoute() {
        convertAdressesToPoints();
        // createRouteFromPoints(); // TODO: proper MVC shit.
        // drawRoute();             // TODO: very(!!) ugly jumping around shit
    }

    //// ---------- convert Addresses to Points ---------- ////

    private void convertAdressesToPoints() {
        convertOriginStringToPoint();
        convertDestinationStringToPoint();
    }

    private void convertOriginStringToPoint() {

        MapboxGeocoding originMapboxGeocoding;

        // set up request
        originMapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA")
                .query(originEditText.getText().toString())
                .build();

        // TODO: refactor Callback!
        // enqueue request
        originMapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                // check for results
                if (results.size() > 0) {
                    // Log first result Point
                    Point firstResultPoint = results.get(0).center();
                    origin = firstResultPoint;
                    isOriginSet = true;
                    if (isDestinationSet) {
                        createRouteFromPoints();
                        isOriginSet = false;
                        isDestinationSet = false;
                    }
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
    }

    private void convertDestinationStringToPoint() {

        MapboxGeocoding destinationMapboxGeocoding;

        // set up request
        destinationMapboxGeocoding = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA")
                .query(destinationEditText.getText().toString())
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
                    destination = firstResultPoint;
                    isDestinationSet = true;
                    if (isOriginSet) {
                        createRouteFromPoints();
                        isOriginSet = false;
                        isDestinationSet = false;
                    }
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
    }

    //// ---------- create route from Points---------- ////

    private void createRouteFromPoints() {
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

                // retrive route from response
                currentRoute = response.body().routes().get(0);

                drawRoute();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //// ---------- draw route---------- ////

    private void drawRoute() {
        directionsRouteFeature = Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
        Log.i(TAG, isOriginSet.toString() + " " + isDestinationSet + " " + currentRoute.toString());
    }

    private void initSource() {

    }

    private void initLayer() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
