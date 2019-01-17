package ubla.org.ubla;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, Style.OnStyleLoaded {

    // Constants
    private static final String TAG = "MainActivity";

    // MapView Object
    private MapView mapView;

    // MapboxMap stuff
    MapboxMap mapboxMap;

    // LocationComponent stuff
    LocationComponent locationComponent;

    // Permission Stuff
    private PermissionsManager permissionsManager;

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

    // ---------- AppCompatActivity Lifecycle Callbacks implementation ---------- //

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
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
