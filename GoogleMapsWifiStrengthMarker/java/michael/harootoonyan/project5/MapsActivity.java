package michael.harootoonyan.project5;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private WifiManager wifiManager;
    private GoogleMap gMap;
    private LatLng positionMarker;
    private ArrayList<LatLng> positionMarkersList;
    private int lineColor;
    private PolylineOptions polylineOptions;
    public TextView wifiSingalTextView;
    private static int GOOD_CONNECTION = -34;
    private static int BAD_CONNECTION = -67;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        wifiSingalTextView = (TextView) findViewById(R.id.wifiSignalTextView);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        runSignalStrengthUpdaterThread();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gMap.moveCamera(CameraUpdateFactory.zoomBy(20));
        positionMarkersList = new ArrayList<LatLng>();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                positionMarker = new LatLng(location.getLatitude(), location.getLongitude());
                if (positionMarkersList.size() % 2 == 0) {
                    polylineOptions = new PolylineOptions();
                    polylineOptions.width(5.0f);
                    if ( !positionMarkersList.isEmpty() ) {
                        LatLng prevMark = positionMarkersList.get(positionMarkersList.size()-1);
                        polylineOptions.add(prevMark);
                    }
                }
                positionMarkersList.add(positionMarker);
                gMap.addMarker(new MarkerOptions().position(positionMarker).title(""));
                polylineOptions.color(lineColor); // always changing every second
                polylineOptions.add(positionMarker);
                gMap.addPolyline(polylineOptions);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(positionMarker));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        try {
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }


    private void runSignalStrengthUpdaterThread() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                while (1 == 1) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            wifiManager.startScan();
                            List<ScanResult> list = wifiManager.getScanResults();
                            int wifiSignalLevel = list.get(0).level;
                            wifiSingalTextView.setText(list.get(0).SSID+" RSSI Level: "+wifiSignalLevel + "\n");

                            if ( GOOD_CONNECTION < wifiSignalLevel) { // Good Connection
                                lineColor = Color.GREEN;
                            }

                            if ( BAD_CONNECTION <= wifiSignalLevel && wifiSignalLevel <= GOOD_CONNECTION) { // OK Connection
                                lineColor = Color.YELLOW;
                            }

                            if ( wifiSignalLevel < BAD_CONNECTION ) { // Bad Connection
                                lineColor = Color.RED;
                            }

                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }




}