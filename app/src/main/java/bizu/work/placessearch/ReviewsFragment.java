package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

public class ReviewsFragment extends Fragment {

    private String placeID;

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
        placeID = activity.getDetailsPlaceID();
//        Log.i("in oncCreate", "------onCreate reivewFragment--------------placeID-------------: " + placeID);

        populateDropdown(v, R.id.review_source, R.array.review_source);
        populateDropdown(v, R.id.review_sort_option, R.array.review_sort_method);

        setSpinnerListeners(v);

//        Button btnClear = (Button) v.findViewById(R.id.btn_clear);

        populateReviews(v, "Google", SortBy.DEFAULT_ORDER);



        return v;
    }

    private void populateDropdown(View v, int spinnerType, int arrayType) {

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

    private void populateReviews(View v, String reviewsFrom, SortBy sortBy) {

        TableLayout table = v.findViewById(R.id.reviews_table);
        table.removeAllViews();
        table.setPadding(0,20, 0,20 );

        Table tableObj = new Table(getActivity(), null, v, null);
        tableObj.populateReviews(table, placeID, reviewsFrom, sortBy);

    }


    /*
    * Set review dropdown click listeners. If review source is selected, get the current sort option
    * selected and vice versa since only one of them would fire a signal at a time.
    * */
    private void setSpinnerListeners(View v) {

        Spinner reviewSourceSpinner = (Spinner) v.findViewById(R.id.review_source);
        reviewSourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                Log.i("in setSpinnerListeners", "------setSpinnerListeners------selected--------item-------------: " + item.toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {

                Object item = parent.getItemAtPosition(0);
                Log.i("in setSpinnerListeners", "------setSpinnerListeners------selected--------item-------------: " + item.toString());
            }
        });

    }
}
