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


        TableRow tr_head = new TableRow(this);
        tr_head.setId(10);
//        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        TextView label_date = new TextView(this);
        label_date.setId(20);
        label_date.setText("DATE");
        label_date.setTextColor(Color.WHITE);
        label_date.setPadding(5, 5, 5, 5);
        tr_head.addView(label_date);// add the column to the table row here

        TextView label_weight_kg = new TextView(this);
        label_weight_kg.setId(21);// define id that must be unique
        label_weight_kg.setText("Wt(Kg.)"); // set the text for the header
        label_weight_kg.setTextColor(Color.WHITE); // set the color
        label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_weight_kg); // add the column to the table row here



        table.addView(tr_head, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        Integer count=0;
        int i = 0;
        while (i < 10) {
            i++;
            String date = "xyz";
            Double weight_kg = -17.0;
// Create the table row
            TableRow tr = new TableRow(this);
            if(count%2!=0) tr.setBackgroundColor(Color.GRAY);
            tr.setId(100+count);
            tr.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));

//Create two columns to add as table data
            // Create a TextView to add date
            TextView placeName = new TextView(this);
            TextView address = new TextView(this);
            RelativeLayout container = new RelativeLayout(this);


            // Place name
            RelativeLayout.LayoutParams placeLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);


            placeName.setPadding(3, 3, 5, 3);
            placeName.setText("University of Southern California");
            placeName.setTypeface(Typeface.DEFAULT_BOLD);
            placeName.setLayoutParams(placeLayout);


            // Address
            RelativeLayout.LayoutParams addressLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);

            addressLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addressLayout.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

            address.setPadding(3, 3, 5, 3);
            address.setText("Los Angeles");
            address.setTypeface(Typeface.DEFAULT_BOLD);
            address.setLayoutParams(new TableRow.LayoutParams(2));
            address.setLayoutParams(addressLayout);



            // the container
            RelativeLayout.LayoutParams containerLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            containerLayout.height = 65;

            container.setLayoutParams(containerLayout);

            container.addView(placeName, new TableRow.LayoutParams(2));
            container.addView(address, new TableRow.LayoutParams(2));







//            TextView labelDATE = new TextView(this);
//            labelDATE.setId(200+count);
//            labelDATE.setText(date);
//            labelDATE.setPadding(2, 0, 5, 0);
//            labelDATE.setTextColor(Color.WHITE);
//            tr.addView(labelDATE);
//            TextView labelWEIGHT = new TextView(this);
//            labelWEIGHT.setId(200+count);
//            labelWEIGHT.setText(weight_kg.toString());
//            labelWEIGHT.setTextColor(Color.WHITE);
////            tr.addView(labelWEIGHT);



            tr.addView(container, new TableRow.LayoutParams(2));

// finally add this to the table row
            table.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            count++;
        }


        return table;


    }

    private TableRow getTableRow() {


        TableRow row = new TableRow(this);
        RelativeLayout container = new RelativeLayout(this);
        TextView placeName = new TextView(this);
        TextView address = new TextView(this);
        ImageView catIcon = new ImageView(this);
        ImageView favIcon = new ImageView(this);


        LinearLayout.LayoutParams iconLayout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        // Category icon
        iconLayout.gravity = Gravity.LEFT;
        iconLayout.setMargins(0, 0, 5, 0);

        catIcon.setPadding(3, 3, 3, 3);
        catIcon.setLayoutParams(new TableRow.LayoutParams(1));
        catIcon.setLayoutParams(iconLayout);
        Drawable icon = getResources().getDrawable(R.drawable.ic_droid);
        catIcon.setImageDrawable(icon);


        // Favorites icon
        LinearLayout.LayoutParams heartLayout = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        heartLayout.gravity = Gravity.LEFT;
        heartLayout.setMargins(5, 0, 10, 0);

        favIcon.setPadding(3, 3, 3, 3);
        favIcon.setLayoutParams(new TableRow.LayoutParams(1));
        favIcon.setLayoutParams(heartLayout);
        Drawable heart = getResources().getDrawable(R.drawable.ic_favorite_plain);
        favIcon.setImageDrawable(heart);


        // Place name
        RelativeLayout.LayoutParams placeLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);


        placeName.setPadding(3, 3, 5, 3);
        placeName.setText("University of Southern California");
        placeName.setTypeface(Typeface.DEFAULT_BOLD);
        placeName.setLayoutParams(new TableRow.LayoutParams(2));
        placeName.setLayoutParams(placeLayout);


        // Address
        RelativeLayout.LayoutParams addressLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        addressLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addressLayout.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

        address.setPadding(3, 3, 5, 3);
        address.setText("Los Angeles");
        address.setTypeface(Typeface.DEFAULT_BOLD);
        address.setLayoutParams(new TableRow.LayoutParams(2));
        address.setLayoutParams(addressLayout);

        // the container
        RelativeLayout.LayoutParams containerLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        containerLayout.height = 65;

        container.setLayoutParams(containerLayout);

        container.addView(placeName);
        container.addView(address);

        // Now put them all together
        row.addView(catIcon);
        row.addView(container);
        row.addView(favIcon);
        row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        return row;
    }






















//        final TextView tvProduct = new TextView(getApplicationContext());
//
//        LinearLayout.LayoutParams lp_l3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (LayoutParams.WRAP_CONTENT));
//        tvProduct.setLayoutParams(lp_l3);
//        tvProduct.setText(product);
//
//        row.addView(tvProduct,  new TableRow.LayoutParams(1));
//        tableContainer.addView(row,new TableLayout.LayoutParams());
//
//        linearMain.addView(tableContainer);




    private void populateResults(String response) {

//        TableLayout table = (TableLayout) findViewById(R.id.main_table);

        TableLayout table = getTableObject();
        for (int i = 0; i < 20; i++) {

            TableRow row = getTableRow();
            table.addView(row, new TableLayout.LayoutParams());
            Log.d("row", row.toString());
        }

        getLayoutInflater().inflate(R.layout.table_layout, table, true);



    }


}



