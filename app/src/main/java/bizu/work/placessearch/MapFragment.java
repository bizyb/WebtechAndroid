package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import db.Database;

public class MapFragment extends Fragment implements OnMapReadyCallback{


    private String response;
    MapView mapView;
    GoogleMap map;
    private String mapFrom;
    private String mapTo;
    private String travelMode;
    private String placeName;
    private String placeID;
    private double destLat;
    private double destLon;


    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Populate the dropdown list and inflate the layout fragment
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        DetailsActivity activity = (DetailsActivity) getActivity();
        placeID = activity.getDetailsPlaceID();
        placeName = new Database(getActivity()).getPlaceName(placeID, "name");
        setLatLng();
//        response = activity.getDetailsData();
        travelMode = "Driving";


        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        populateDropdown(v);
        setSpinnerListeners(v);


        return v;
    }

    private void setLatLng() {

        Database db = new Database(getActivity());
        String lat = db.getPlaceName(placeID, "latitude");
        String lng = db.getPlaceName(placeID, "longitude");
        destLat = Double.parseDouble(lat);
        destLon = Double.parseDouble(lng);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        MapsInitializer.initialize(getActivity());
        LatLng place = new LatLng(destLat, destLon);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12));

        map.addMarker(new MarkerOptions()
                .title(placeName)
                .position(place));

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }




    public void populateDropdown(View v) {

        String[] values = getResources().getStringArray(R.array.driving_mode);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner_map);

        if (spinner == null) {
            spinner = getActivity().findViewById(R.id.spinner_map);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

    }

    private void setSpinnerListeners(View v) {

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner_map);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Object item = parentView.getItemAtPosition(position);
                travelMode = item.toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // this doesn't really apply
                travelMode = "Driving";
            }

        });

    }
}