package bizu.work.placessearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import db.Database;


public class ResultsActivity extends AppCompatActivity implements PaginationLoader {

    private Toolbar toolbar;
    private ProgressDialog progressBar;
    private  int pageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pageNum = 0;

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
        String resultType = intent.getStringExtra("resultType");
        populateResults(response, resultType, -1);

    }

    private void populateResults(String response, String resultType, int pageFromDB) {


        Log.i("pagination", "in ppopulateResults pageFromDB=---------------------------    " + pageFromDB);
        if (resultType.equals("SEARCH_RESULTS")) {

            // we have a new search query. Drop all existing entries, except for thos
            // that have been favorited
            Database db = new Database(this);
            db.dropRows();
            pageNum = 1;

        }

        Table tableObj = new Table(this, response);
        TableLayout table = tableObj.populateTable("results", pageFromDB);

        Paginator paginator = new Paginator(this, response, this);

        getLayoutInflater().inflate(R.layout.table_layout, table, true);
        if (pageFromDB > 0) {
            paginator.showPagination(pageFromDB);
        }
        else {
            paginator.showPagination(pageNum);
        }



    }


    public void loadPaginatedPage(Button prevBtn, Button nextBtn) {


        if (nextBtn != null) {
            String next_page_token = (String) nextBtn.getTag();
            Log.i("token", "next_page_token-----------------length-------------------: " + next_page_token.length() + "");
            Log.i("token", "next_page_token------------------------------------: " + next_page_token + "");
            if (next_page_token.length() > 1) {
                // we have API token so make a new request
                getNewResult(next_page_token);
            }
            else {
                pageNum = Integer.parseInt((String)nextBtn.getTag());
                pageNum++;
                populateResults(null, "PAGINATION", pageNum);
            }

        }
        else {

            //TODO: need to assert if pagename > 0?

            pageNum = (Integer) prevBtn.getTag();
            pageNum--; // the page number to load; prevBtn stores the page number of the page it
                        // currently resides in
            populateResults(null, "PAGINATION", pageNum);
        }


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
                        pageNum++;
                        populateResults(response.toString(), "PAGINATION", -1);

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

}



