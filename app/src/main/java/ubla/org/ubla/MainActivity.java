package ubla.org.ubla;

import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    // Constants
    private static final String TAG = "MainActivity";

    // MapView Object
    private MapView mapView;

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
            // TODO: LocationComponent must be called here
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    //// ---------- LocationListener Callbacks implementation ---------- ////

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            // TODO: LocationComponent must be called here
        } else {
            Log.e(TAG, "User has not granted permissions.");
        }
    }

    // TODO: Is this necessary??
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // ---------- OnMapReadyCallback implementation ---------- //

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

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
