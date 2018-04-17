package bizu.work.placessearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

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

    private TableLayout getTableObject() {

        TableLayout table = (TableLayout) findViewById(R.id.main_table);
        table.setPadding(0,200, 0,50 );
//        table.setMinimumWidth(7000);


//        TableRow tr_head = new TableRow(this);

        Integer count=0;
        int i = 0;
        while (i < 10) {
            i++;

            TableRow tr = new TableRow(this);
            tr.setPadding(50, 0, 0, 0);

            TextView placeName = new TextView(this);
            ImageView favIcon = new ImageView(this);
            ImageView catIcon = new ImageView(this);

            LinearLayout.LayoutParams iconLayout = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            // Category icon
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

            String sourceString = "<strong>University of Southern California, Arts and Sciences</strong>";
            sourceString += "<br>Los Angeles"; //+ id + "</b> " + name;
            placeName.setText(Html.fromHtml(sourceString));

            placeName.setMaxWidth(1000);
            placeName.setMinWidth(1000);
            placeName.setMinHeight(250);

            tr.addView(catIcon, new TableRow.LayoutParams(1));
            tr.addView(placeName, new TableRow.LayoutParams(2));
            tr.addView(favIcon, new TableRow.LayoutParams(3));
            tr.setPadding(0,50,20,5);

            table.addView(tr);
            count++;
        }


        return table;


    }


    private void populateResults(String response) {

//        TableLayout table = (TableLayout) findViewById(R.id.main_table);

        TableLayout table = getTableObject();
//        for (int i = 0; i < 20; i++) {
//
//            TableRow row = getTableRow();
//            table.addView(row, new TableLayout.LayoutParams());
//            Log.d("row", row.toString());
//        }

        getLayoutInflater().inflate(R.layout.table_layout, table, true);



    }


}



