package bizu.work.placessearch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


public class ResultsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add a back button and a listener
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });


        Intent intent = getIntent();
        String response = intent.getStringExtra("response");
//        String resultType = intent.getStringExtra("resultType");

        populateResults(response);

    }

    private TableRow getTableRow(JSONObject r) {

        TableRow tr = null;
        try {

               String name = r.getString("name");
               String vicinity = r.getString("vicinity");
               String iconURL = r.getString("icon");

               tr = new TableRow(this);
               tr.setPadding(50, 0, 0, 0);

               TextView placeName = new TextView(this);
               ImageView favIcon = new ImageView(this);
               ImageView catIcon = new ImageView(this);


               // Category icon
               LinearLayout.LayoutParams iconLayout = new LinearLayout.LayoutParams(
                       LayoutParams.WRAP_CONTENT,
                       LayoutParams.WRAP_CONTENT);

               iconLayout.gravity = Gravity.LEFT;

               catIcon.setPadding(0, 3, 50, 3);
               catIcon.setLayoutParams(new TableRow.LayoutParams(1));
               catIcon.setLayoutParams(iconLayout);
               catIcon.setMinimumHeight(150);
               catIcon.setMinimumWidth(150);
               setIcon(catIcon, iconURL);
               catIcon.setScaleType(ImageView.ScaleType.FIT_XY);


               // Favorites icon

               favIcon.setPadding(50, 10, 50, 3);
               favIcon.setLayoutParams(new TableRow.LayoutParams(1));
               Drawable heart = getResources().getDrawable(R.drawable.ic_favorite_plain);
               favIcon.setImageDrawable(heart);

               // Place name
                String nameNaddr = "<strong>" + name + "</strong>";
                nameNaddr += "<br>" + vicinity;
               placeName.setText(Html.fromHtml(nameNaddr));

               placeName.setMaxWidth(1000);
               placeName.setMinWidth(1000);
               placeName.setMinHeight(200);

               tr.addView(catIcon, new TableRow.LayoutParams(1));
               tr.addView(placeName, new TableRow.LayoutParams(2));
               tr.addView(favIcon, new TableRow.LayoutParams(3));
               tr.setPadding(0, 50, 20, 5);
           }
           catch(Exception e){
               // TODO: output no results/failed to get results error here
               Log.d("error", e.toString());
           }

            return tr;
    }

    private void setIcon(ImageView catIcon, String iconURL) {

        Picasso.get().load(iconURL).into(catIcon);
    }


    private void populateResults(String response) {

        TableLayout table = (TableLayout) findViewById(R.id.main_table);
        table.removeAllViews();
        table.setPadding(0,200, 0,50 );

        try {

            JSONObject responseJSON = new JSONObject(response);
            JSONArray results = responseJSON.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                JSONObject r = results.getJSONObject(i);
                TableRow row = getTableRow(r);
                table.addView(row);
            }

            getLayoutInflater().inflate(R.layout.table_layout, table, true);

            // Show previous and next buttons if applicable
            showPagination(responseJSON);
        }

        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

    }

    private void showPagination(JSONObject r) {

        RelativeLayout paginator = findViewById(R.id.pagination_container);
        getLayoutInflater().inflate(R.layout.pagination,  paginator, true);

        // handle prev button
        //TODO: set listener if previous page available and make it clickable
        Button prevBtn = (Button) findViewById(R.id.btn_prev);
        prevBtn.setEnabled(false);
        prevBtn.setClickable(false);


        Button nextBtn = (Button) findViewById(R.id.btn_next);
        nextBtn.setEnabled(false);
        nextBtn.setClickable(false);

        try {

            String next_page_token = r.getString("next_page_token");

            if (next_page_token != null) {

                nextBtn.setEnabled(true);
                nextBtn.setClickable(true);

                nextBtn.setTag(next_page_token);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        loadPaginatePage();
                    }
                });

            }
        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

    }


    private void loadPaginatePage() {

        Button nextBtn = (Button) findViewById(R.id.btn_next);
        String next_page_token = (String) nextBtn.getTag();
        getNewResult(next_page_token);

    }

    private void getNewResult(String next_page_token) {

        //Make a new request for paginated results
        RequestQueue queue = Volley.newRequestQueue(this);

        String location = "1,1"; //deprecated
        String curr_page_num = "1"; //deprecated
        String queryString = "?pagetoken=" + next_page_token + "&location=" + location;
        queryString += "&curr_page_num=" + curr_page_num;

        final String url = "http://bizyb.us-east-2.elasticbeanstalk.com/search-endpoint" + queryString;
        Log.d("url", url);

        JsonObjectRequest getRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        progressBar.dismiss();
                        populateResults(response.toString());

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
        queue.add(getRequest);
        showProgressBar();

    }

    public void showProgressBar() {

        String msg = getResources().getString(R.string.fetching_results);

        progressBar = new ProgressDialog(this, R.style.FetchingResultsStyle);
        progressBar.setCancelable(true);
        progressBar.setMessage(msg);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        new Thread(new Runnable() {

            public void run() {

            }
        }).start();
    }
//    public void search() {
//
//        String queryString = getGETParams();
//        RequestQueue queue = Volley.newRequestQueue(view.getContext());
//
//
//        final String url = "http://bizyb.us-east-2.elasticbeanstalk.com/search-endpoint" + queryString;
////        final String url = "http://ip-api.com/json";
//        Log.d("url", url);
//
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        Log.d("Response", response.toString());
//                        Intent resultsIntent = new Intent(activity, ResultsActivity.class);
//                        resultsIntent.putExtra("response", response.toString());
//                        resultsIntent.putExtra("resultType", "SEARCH_RESULTS");
//                        progressBar.dismiss();
//                        activity.startActivity(resultsIntent);
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", error.toString());
//                        progressBar.dismiss();
//                    }
//                }
//        );
//
//        // add it to the RequestQueue
//        queue.add(getRequest);
//        showProgressBar();
//    }


}



