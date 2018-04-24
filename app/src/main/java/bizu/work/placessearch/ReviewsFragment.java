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
    String reviewsFromGlobal;
    SortBy sortByGlobal;

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
        reviewsFromGlobal = "Google";
        sortByGlobal = SortBy.DEFAULT_ORDER;
        populateReviews(v, reviewsFromGlobal, sortByGlobal);



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

//        populateDropdown(v, R.id.review_source, R.array.review_source);
//        populateDropdown(v, R.id.review_sort_option, R.array.review_sort_method);

        Spinner reviewSource = (Spinner) v.findViewById(R.id.review_source);
        Spinner sortingOption = (Spinner) v.findViewById(R.id.review_sort_option);

        reviewSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Object item = parentView.getItemAtPosition(position);
                String value = item.toString();
                switch(value) {
                    case "Google reviews":
                        reviewsFromGlobal = "Google";
                        break;
                    case "Yelp reviews":
                        reviewsFromGlobal = "Yelp";
                        break;
                    default:
                        reviewsFromGlobal = "Google";
                        break;
                }
                Log.i("reviewsFromGlobal", "------reviewsFromGlobal------reviewsFromGlobal--------reviewsFromGlobal-------------: " + reviewsFromGlobal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // this doesn't really apply
                reviewsFromGlobal = "Google";
            }

        });

        sortingOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                Object item = parentView.getItemAtPosition(position);
                String sortBy = item.toString();
                Log.i("sortBy", "------sortBy------sortBy--------sortBy-------------: " + sortBy);
                switch (sortBy) {

                    case "Default Order":
                        sortByGlobal = SortBy.DEFAULT_ORDER;
                        break;
                    case "Highest Rating":
                        sortByGlobal = SortBy.HIGHEST_RATING;
                        break;
                    case "Lowest Rating":
                        sortByGlobal = SortBy.LOWEST_RATING;
                        break;
                    case "Most Recent":
                        sortByGlobal = SortBy.MOST_RECENT;
                        break;
                    case "Least Recent":
                        sortByGlobal = SortBy.LEAST_RECENT;
                        break;
                    default:
                        sortByGlobal = SortBy.DEFAULT_ORDER;
                        break;
                    }
                }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // this doesn't really apply
                sortByGlobal = SortBy.DEFAULT_ORDER;
            }

        });
    }
}

//
