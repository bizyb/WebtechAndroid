package bizu.work.placessearch;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

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

    private String getGETParams() {

        String keywordStr;
        String distanceStr;
        String customLoc;
        String centerLat;
        String centerLon;
        String category;
        Spinner spinner;

        Map<String, String> GETParams = new HashMap<String, String>();

        EditText keyword = (EditText) activity.findViewById(R.id.keyword_input);;
        EditText distance = (EditText) activity.findViewById((R.id.distance_input));
        EditText otherLocInput = (EditText) activity.findViewById(R.id.other_loc_input);
        RadioButton currLocBtn = (RadioButton) activity.findViewById(R.id.radio_current_loc);
        RadioButton otherLocBtn = (RadioButton) activity.findViewById(R.id.radio_other_loc);

        keywordStr = keyword.getText().toString();
        distanceStr = distance.getText().toString();
        customLoc = "-1";
        centerLat = "51.503186";
        centerLon = "-0.126446";
//        currLoc = getCurrentLocation();

        if (otherLocBtn.isChecked()) {

            customLoc = otherLocInput.getText().toString();
        }

        if (distanceStr.length() == 0) {

            distanceStr = "10";
        }
        spinner = (Spinner) view.findViewById(R.id.spinner);
        if (spinner == null) {category = "Default"; }
        else {
            category = spinner.getSelectedItem().toString();
        }


        GETParams.put("keyword", keywordStr);
        GETParams.put("category", category);
        GETParams.put("distance", distanceStr);
        GETParams.put("curr_page_num", 0 + "");
        GETParams.put("customLoc", customLoc);
        GETParams.put("centerLat", centerLat);
        GETParams.put("centerLon", centerLon);

        String queryString = "?";

        for (Map.Entry<String, String> entry: GETParams.entrySet()) {

            queryString += entry.getKey() + "=" + entry.getValue() + "&";

        }
        queryString += "requestType=" + "nearbyPlaces";
        return queryString;
    }

    public void search() {

        String queryString = getGETParams();
        RequestQueue queue = Volley.newRequestQueue(view.getContext());


        final String url = "http://bizyb.us-east-2.elasticbeanstalk.com/search-endpoint" + queryString;
        Log.d("url", url);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
        showProgressBar();
    }

    public void showProgressBar() {

        String msg = activity.getResources().getString(R.string.fetching_results);

        progressBar = new ProgressDialog(view.getContext(), R.style.FetchingResultsStyle);
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
