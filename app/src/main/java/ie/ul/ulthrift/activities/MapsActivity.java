package ie.ul.ulthrift.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import ie.ul.ulthrift.R;
import ie.ul.ulthrift.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<Marker> markers = new ArrayList<>();
    private List<LatLng> markerLocations = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MapPreferences";
    private static final String MARKER_LOCATIONS_KEY = "markerLocations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set up map interaction features
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        LatLng location = new LatLng(52.6736, -8.5724);
        googleMap.addMarker(new MarkerOptions().position(location).title("University of Limerick"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,16));

        // Load marker locations from SharedPreferences
        loadMarkerLocations();

        // Add markers to the map
        for (LatLng latLng : markerLocations) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
        }

        // Move camera to the last saved marker location
        if (!markerLocations.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLocations.get(markerLocations.size() - 1)));
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Handle map click event

        // Add a marker at the clicked location
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location").draggable(true));
        markers.add(marker);

        // Save marker locations to SharedPreferences
        saveMarkerLocations();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Remove the clicked marker from the map and from the list of markers
        marker.remove();
        markers.remove(marker);
        // Update marker locations in SharedPreferences
        saveMarkerLocations();
        return true; // Return true to consume the event and prevent the default behavior (showing marker info window)
    }

    private void saveMarkerLocations() {
        Set<String> markerLocationsSet = new HashSet<>();
        for (Marker marker : markers) {
            LatLng position = marker.getPosition();
            markerLocationsSet.add(position.latitude + "," + position.longitude);
        }
        sharedPreferences.edit().putStringSet(MARKER_LOCATIONS_KEY, markerLocationsSet).apply();
    }

    private void loadMarkerLocations() {
        Set<String> markerLocationsSet = sharedPreferences.getStringSet(MARKER_LOCATIONS_KEY, null);
        if (markerLocationsSet != null) {
            for (String locationString : markerLocationsSet) {
                String[] parts = locationString.split(",");
                double lat = Double.parseDouble(parts[0]);
                double lng = Double.parseDouble(parts[1]);
                LatLng latLng = new LatLng(lat, lng);
                markerLocations.add(latLng);
            }
        }
    }
}
