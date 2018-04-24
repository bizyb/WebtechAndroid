package bizu.work.placessearch;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchServices {

    private ProgressDialog progressBar;
    private Activity activity;
    private View view;

    public SearchServices(Activity activity, View view) {

            this.activity = activity;
            this.view = view;
    }

    private String getCurrentLocation() {

        return "-1";
    }

    private String getGETParams(JSONObject formData) {

//        String keywordStr;
//        String distanceStr;
//        String customLoc;
//        String centerLat;
//        String centerLon;
//        String category;
//        Spinner spinner;
//
//        Map<String, String> GETParams = new HashMap<String, String>();
//
//        EditText keyword = (EditText) activity.findViewById(R.id.keyword_input);;
//        EditText distance = (EditText) activity.findViewById((R.id.distance_input));
//        EditText otherLocInput = (EditText) activity.findViewById(R.id.other_loc_input);
//        RadioButton currLocBtn = (RadioButton) activity.findViewById(R.id.radio_current_loc);
//        RadioButton otherLocBtn = (RadioButton) activity.findViewById(R.id.radio_other_loc);
//
//        keywordStr = keyword.getText().toString();
//        distanceStr = distance.getText().toString();
//        customLoc = "-1";
//        centerLat = "34.0266 ";
//        centerLon = "-118.2831";
//
//
////        currLoc = getCurrentLocation();
//
//        if (otherLocBtn.isChecked()) {
//
//            customLoc = otherLocInput.getText().toString();
//        }
//
//        if (distanceStr.length() == 0) {
//
//            distanceStr = "10";
//        }
//        spinner = (Spinner) view.findViewById(R.id.spinner);
//        if (spinner == null) {category = "Default"; }
//        else {
//            category = spinner.getSelectedItem().toString();
//        }
//
//
//        GETParams.put("keyword", keywordStr);
//        GETParams.put("category", category);
//        GETParams.put("distance", distanceStr);
//        GETParams.put("curr_page_num", 0 + "");
//        GETParams.put("customLoc", customLoc);
//        GETParams.put("centerLat", centerLat);
//        GETParams.put("centerLon", centerLon);

        String queryString = "?";

        try {

            queryString += "keyword=" + formData.getString("keyword");
            queryString += "&distance=" + formData.getString("distance");
            queryString += "&customLoc=" + formData.getString("customLoc");
            queryString += "&category=" + formData.getString("category");
            queryString += "&centerLat=" + formData.getString("centerLat");
            queryString += "&centerLon=" + formData.getString("centerLon");
            queryString += "&requestType=" + formData.getString("nearbyPlaces");

//            formData.put("keyword", keyword.getText().toString());
//            formData.put("distance", distanceValue);
//            formData.put("customLoc", otherLocValue);
//            formData.put("category", categoryValue);
//            formData.put("centerLat", centerLat);
//            formData.put("centerLon", centerLon);
        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.e("error", e.toString());
//            Log.i("in populateTable", "populateTable--------------------3--------------");
        }

//        String queryString = "?";
//
//        for (Map.Entry<String, String> entry: GETParams.entrySet()) {
//
//            queryString += entry.getKey() + "=" + entry.getValue() + "&";
//
//        }
//        queryString += "requestType=" + "nearbyPlaces";
        return queryString;
    }

    public void search(final String placeID, final JSONObject formData) {

        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "";

        if (placeID != null) {
            url = "http://bizyb.us-east-2.elasticbeanstalk.com/places-details-endpoint";
            url += "?placeID=" + placeID;

            //TODO: check if details (not just nearby results) are already in db; if so, load that.

        }
        else {

            String queryString = getGETParams(formData);
            url = "http://bizyb.us-east-2.elasticbeanstalk.com/search-endpoint" + queryString;
        }

//        final String url = "http://ip-api.com/json";
        Log.d("url", url);

        Log.i("in search", "search--------------------placeID-------------: " + placeID);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Intent intent = new Intent(activity, ResultsActivity.class);
                        Log.d("Response", response.toString());

                        if (placeID == null) {

                            intent.putExtra("resultType", "SEARCH_RESULTS");
                        }
                        else {

                            Log.i("in search", "search------about to load DetailAcitivity--------------placeID-------------: " + placeID);
                            intent =  new Intent(activity, DetailsActivity.class);
                            intent.putExtra("placeID", placeID);
                            intent.putExtra("loadFromDB", "false");
                        }
                        intent.putExtra("response", response.toString());
                        progressBar.dismiss();
                        activity.startActivity(intent);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        progressBar.dismiss();
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
        showProgressBar();
    }


    public void showProgressBar() {

        String msg = activity.getResources().getString(R.string.fetching_results);

        progressBar = new ProgressDialog(activity, R.style.FetchingResultsStyle);
        progressBar.setCancelable(true);
        progressBar.setMessage(msg);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        new Thread(new Runnable() {

            public void run() {

            }
        }).start();
    }


}
