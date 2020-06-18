package com.isoguzay.inappcodelab.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.maps.util.LogM;
import com.isoguzay.inappcodelab.R;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapViewDemo";
    private static final int REQUEST_CODE = 100;
    //Huawei map
    private HuaweiMap hMap;
    private MapView mMapView;

    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (!hasPermissions(this, RUNTIME_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE);
        }

        //get mapview instance
        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        //get map instance
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        // map settings
        huaweiMap.isMyLocationEnabled();
        huaweiMap.getUiSettings().setMyLocationButtonEnabled(true);
        huaweiMap.getUiSettings().setCompassEnabled(true);
        huaweiMap.getUiSettings().setAllGesturesEnabled(true);
        huaweiMap.getUiSettings().setRotateGesturesEnabled(true);
        huaweiMap.getUiSettings().setScrollGesturesEnabled(true);
        huaweiMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        huaweiMap.getUiSettings().isZoomGesturesEnabled();
        huaweiMap.getUiSettings().isZoomControlsEnabled();
        huaweiMap.setMyLocationEnabled(true);

        // add a marker to map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Galatasaray Ali Samiyen Stadium");
        markerOptions.snippet("Football");
        markerOptions.position(new LatLng(41.103931, 28.990642));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_place_24));
        huaweiMap.addMarker(markerOptions);

        hMap = huaweiMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
