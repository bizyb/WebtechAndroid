package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback{


    private String response;
    MapView mapView;
    GoogleMap map;


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
        response = activity.getDetailsData();


        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);


        return v;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        MapsInitializer.initialize(getActivity());
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));


//        map.setMyLocationEnabled(true);
//        CameraUpdate camera = CameraUpdateFactory.newLatLng(sydney);
//        map.animateCamera(camera);


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
}