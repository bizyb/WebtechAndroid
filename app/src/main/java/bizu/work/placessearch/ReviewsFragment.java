package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import db.Database;

public class ReviewsFragment extends Fragment {

    private String response;

    public ReviewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Populate the dropdown list and inflate the layout fragment
        View v = inflater.inflate(R.layout.reviews_fragment, container, false);

        DetailsActivity activity = (DetailsActivity) getActivity();
        response = activity.getDetailsData();

        populateDropdown(v, R.id.review_source, R.array.review_source);
        populateDropdown(v, R.id.review_sort_option, R.array.review_sort_method);

        return v;
    }

    public void populateDropdown(View v, int spinnerType, int arrayType) {

        String[] values = getResources().getStringArray(arrayType);
        Spinner spinner = (Spinner) v.findViewById(spinnerType);

        if (spinner == null) {
            spinner = getActivity().findViewById(spinnerType);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

    }

}
