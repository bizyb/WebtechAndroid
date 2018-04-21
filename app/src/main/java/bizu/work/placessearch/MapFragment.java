package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import com.google.android.gms.maps*;

public class MapFragment extends Fragment {


    private String response;
//    private GoogleMap map;

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

//        populatePhotosTab(v);

        return v;
    }
}