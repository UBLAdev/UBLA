package ubla.org.ubla;

import android.app.PendingIntent;
import android.location.LocationProvider;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    // Constants
    private final static String TAG = "MainActivity";

    // MapView Object
    private MapView mapView;

    // Mapbox stuff
    private MapboxMap mapboxMap;

    // Permissions Stuff
    PermissionsManager permissionsManager;
    PermissionsListener permissionsListener = new PermissionsListener() {
        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain) {

        }

        @Override
        public void onPermissionResult(boolean granted) {
            if (granted) {
                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            } else {
                // User denied the permission
            }
        }
    };

    // Buttons
    Button getLocationButton;

    // Location stuff
    private long getLocationIntervalInMillis = 10000;
    private long maxLocationWaitTimeInMillis = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get Instance of MapView
        // TODO: Refactor hardcoded API key!!
        Mapbox.getInstance(this, "pk.eyJ1IjoidWJsYSIsImEiOiJjanF6dHJvd3EwaTdpNDNvMDlydHRzOHVhIn0.yf8AdRE2gR9NH80fSYZjBA");
        setContentView(R.layout.activity_main);

        // get MapView and prepare it for use
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // get Buttons and prepare for use
        getLocationButton = findViewById(R.id.getLocationButton);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        // set up permission stuff
        // TODO: Should probably go into inCreate()?
        permissionsManager = new PermissionsManager(this);
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

        // Set up LocationEngine
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        /*
        // Create and and OnClickListener to getLocationButton
        getLocationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // request location
                LocationEngineRequest locationEngineRequest = new LocationEngineRequest()
                        .Builder(getLocationIntervalInMillis)
                            .setPriority(LocationEngineRequest.PRIORITY_LOW_POWER)
                            .setMaxWaitTime(maxLocationWaitTimeInMillis)
                            .build();
            }
        });
        */
    }

    // TODO: really necessary?
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent();
        } else {
            Log.e(TAG, "User Permission was not granted.");
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        enableLocationComponent();
    }

    @SuppressWarnings( {"MissingPermission"} )
    private void enableLocationComponent() {
        // check whether permissions are granted and otherwise ask for them
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions locationComponentOptions = LocationComponentOptions
                    .builder(this)
                    .trackingGesturesManagement(true)
                    .accuracyColor(ContextCompat.getColor(this, R.color.mapbox_blue))
                    .build();

            // get an instance of the Component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // activate with options, should not be necessary!?!
            // TODO: fix that!!
            locationComponent.activateLocationComponent(this, );

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            Log.i(TAG, locationComponent.getLocationComponentOptions().toString());

            // Set the components camera mode
            // locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
}
