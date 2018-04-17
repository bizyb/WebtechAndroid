package bizu.work.placessearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;


import org.json.JSONArray;
import org.json.JSONObject;


public class ResultsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

               catIcon.setPadding(0, 3, 120, 3);
               catIcon.setLayoutParams(new TableRow.LayoutParams(1));
               catIcon.setLayoutParams(iconLayout);
               Drawable icon = getResources().getDrawable(R.drawable.ic_droid);
               catIcon.setImageDrawable(icon);
               catIcon.setMaxWidth(50);


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


    private void populateResults(String response) {

        TableLayout table = (TableLayout) findViewById(R.id.main_table);
        table.setPadding(0,200, 0,50 );

        try {

            JSONObject responseJSON = new JSONObject(response);
            JSONArray results = responseJSON.getJSONArray("results");
//            Log.d("json", (JSONArray) responseJSON["as"].toString());


            for (int i = 0; i < results.length(); i++) {

                JSONObject r = results.getJSONObject(i);

//                Log.d("result row", r.toString());
                TableRow row = getTableRow(r);
                table.addView(row);
            }

            getLayoutInflater().inflate(R.layout.table_layout, table, true);
        }

        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }



    }


}



