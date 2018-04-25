package bizu.work.placessearch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.w3c.dom.Text;

import java.util.ArrayList;

import db.Database;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, DirectionCallback{


    private String response;
    MapView mapView;
    GoogleMap googleMap;
    private String mapFrom;
    private String mapTo;
    private String travelMode;
    private String placeName;
    private String placeID;
    private double destLat;
    private double destLon;
    private final String MAP_API_KEY = "AIzaSyA_NhluopOgKm1DhlpxCZebkdwgPqOfItQ";
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private LatLng destination = new LatLng(37.7814432, -122.4460177);
    private TextView btnRequestDirection;
    private EditText mapFromInput;


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
//        setInputListeners(v);


        btnRequestDirection = v.findViewById(R.id.map_from_label);
        btnRequestDirection.setOnClickListener(this);


        PlacesAutocompleteTextView autoComplete = v.findViewById(R.id.map_from_input);
        setMapListener(autoComplete);
//        box.setOnPlaceSelectedListener(autoCompleteListener)
//        box.setOnPlaceSelectedListener(this);
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("setInputListeners", "---setInputListeners-----inside-------    " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("error", "An error occurred: " + status);
//            }
//        });





        return v;
    }

//    @Override
//    public void onPlaceSelected(Place place) {
//        // TODO: Get info about the selected place.
//        Log.i("setInputListeners", "---setInputListeners-----inside-------    " + place.getName());
//    }
//
//    @Override
//    public void onError(Status status) {
//        // TODO: Handle the error.
//        Log.i("error", "An error occurred: " + status);
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.map_from_label) {
            requestDirection();
        }
    }

    private void setLatLng() {

        Database db = new Database(getActivity());
        String lat = db.getPlaceName(placeID, "latitude");
        String lng = db.getPlaceName(placeID, "longitude");
        destLat = Double.parseDouble(lat);
        destLon = Double.parseDouble(lng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        MapsInitializer.initialize(getActivity());
        LatLng place = new LatLng(destLat, destLon);

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12));

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .title(placeName)
                .position(place));
        marker.showInfoWindow();

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Snackbar.make(btnRequestDirection, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);
            googleMap.addMarker(new MarkerOptions().position(origin));
            googleMap.addMarker(new MarkerOptions().position(destination));

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
            setCameraWithCoordinationBounds(route);

            btnRequestDirection.setVisibility(View.GONE);
        } else {
            Snackbar.make(btnRequestDirection, direction.getStatus(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Snackbar.make(btnRequestDirection, t.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    public void requestDirection() {

        Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(MAP_API_KEY)
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//
//        Log.i("onMapReady", "----placeName-----placeName-------    " + placeName);
//        MapsInitializer.initialize(getActivity());
//        LatLng place = new LatLng(destLat, destLon);
//
//        map.setMyLocationEnabled(true);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 12));
//
//        Marker marker = map.addMarker(new MarkerOptions()
//                .title(placeName)
//                .position(place));
//        marker.showInfoWindow();
//
//    }

//    public void getDirections(final GoogleMap map) {
//
//        LatLng origin = new LatLng(37.7849569, -122.4068855);
//        LatLng destination = new LatLng(37.7814432, -122.4460177);
//        GoogleDirection.withServerKey(MAP_API_KEY)
//                .from(origin)
//                .to(destination)
//                .execute(new DirectionCallback() {
//                    @Override
//                    public void onDirectionSuccess(Direction direction, String rawBody) {
//                        Route route = direction.getRouteList().get(0);
//                        Leg leg = route.getLegList().get(0);
//                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
//                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED);
//                        map.addPolyline(polylineOptions);
//                    }
//
//                    @Override
//                    public void onDirectionFailure(Throwable t) {
//                        // Do something here
//                    }
//                });
//
//
//    }

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

//    public void setMapListener(PlacesAutocompleteTextView place) {
//
//
//        AutoCompleteTextView auto = (AutoCompleteTextView) place;
//        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                final String item = parent.getSelectedItem().toString();
//                if (item != null) {
//                    Log.i("setInputListeners", "---selected item-----------------------    " + item);
//                }
////
//            }
//        });
//    }

    public void setMapListener(final PlacesAutocompleteTextView autoComplete) {


//        AutoCompleteTextView auto = (AutoCompleteTextView) autoComplete;

        autoComplete.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
            @Override
            public void onPlaceSelected(@NonNull com.seatgeek.placesautocomplete.model.Place place) {

                autoComplete.getDetailsFor(place, new DetailsCallback() {
                    @Override
                    public void onSuccess(final PlaceDetails details) {

                        double lat = details.geometry.location.lat;
                        Log.i("setInputListeners", "---selected item-----------------------    " + lat + "");
//                        Log.d("test", "details " + details);
//                        mStreet.setText(details.name);
//                        for (AddressComponent component : details.address_components) {
//                            for (AddressComponentType type : component.types) {
//                                switch (type) {
//                                    case STREET_NUMBER:
//                                        break;
//                                    case ROUTE:
//                                        break;
//                                    case NEIGHBORHOOD:
//                                        break;
//                                    case SUBLOCALITY_LEVEL_1:
//                                        break;
//                                    case SUBLOCALITY:
//                                        break;
//                                    case LOCALITY:
//                                        mCity.setText(component.long_name);
//                                        break;
//                                    case ADMINISTRATIVE_AREA_LEVEL_1:
//                                        mState.setText(component.short_name);
//                                        break;
//                                    case ADMINISTRATIVE_AREA_LEVEL_2:
//                                        break;
//                                    case COUNTRY:
//                                        break;
//                                    case POSTAL_CODE:
//                                        mZip.setText(component.long_name);
//                                        break;
//                                    case POLITICAL:
//                                        break;
//                                }
//                            }
//                        }
                    }

//            @Override
//            public void onPlaceSelected(Place place) {
//
////                String item = place.toString();
////                if (item != null) {
//                double lat = place.getPlace().latitude;
//                    Log.i("setInputListeners", "---selected item-----------------------    " + lat + "");
////                }
//            }



                    @Override
                    public void onFailure(final Throwable failure) {
                        Log.d("test", "failure " + failure);
                        Log.e("setInputListeners", "---ERROR----------------------------------ERROR------");
                    }
                });
            }
        });
    }

}