package bizu.work.placessearch;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import db.Database;

public class Table {

    private String response;
    private Activity activity;
    private int pageNum;


    public Table(Activity activity, String response) {

        this.activity = activity;
        this.response = response;
        pageNum = 0;
    }

    private TableRow getTableRow(String name, String vicinity, String iconURL) {

        //TODO: set listeners for both search resutls and favorites
        TableRow tr;

        tr = new TableRow(activity);
        tr.setPadding(50, 0, 0, 0);

        TextView placeName = new TextView(activity);
        ImageView favIcon = new ImageView(activity);
        ImageView catIcon = new ImageView(activity);


        // Category icon
        LinearLayout.LayoutParams iconLayout = new LinearLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        iconLayout.gravity = Gravity.LEFT;

        catIcon.setPadding(0, 3, 50, 3);
        catIcon.setLayoutParams(new TableRow.LayoutParams(1));
        catIcon.setLayoutParams(iconLayout);
        catIcon.setMinimumHeight(150);
        catIcon.setMinimumWidth(150);
        setIcon(catIcon, iconURL);
        catIcon.setScaleType(ImageView.ScaleType.FIT_XY);


        // Favorites icon

        //todo: parameterize icon selection based on whether or not the row is in the favorites db table

        favIcon.setPadding(50, 10, 50, 3);
        favIcon.setLayoutParams(new TableRow.LayoutParams(1));
        Drawable heart = activity.getResources().getDrawable(R.drawable.ic_favorite_plain);
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


        return tr;
    }


    private void setIcon(ImageView catIcon, String iconURL) {

        Picasso.get().load(iconURL).into(catIcon);
    }

    public TableLayout populateTable(String tableFor, int pageFromDB) {

        TableLayout table = (TableLayout) activity.findViewById(R.id.main_table);
        table.removeAllViews();
        table.setPadding(0,200, 0,50 );

        try {

            if (tableFor == "results") {

                String name;
                String vicinity;
                String iconURL;
                JSONArray results;

                if (pageFromDB > 0) {

                    Log.i("in populateTable", "calling getDBPage----------------------------------");
                    Database db = new Database(activity);
                    results = db.getDBPage(pageFromDB);

                }
                else {

                    JSONObject responseJSON = new JSONObject(response);
                    results = responseJSON.getJSONArray("results");
                }

                for (int i = 0; i < results.length(); i++) {

                    JSONObject r = results.getJSONObject(i);
                    name = r.getString("name");
                    vicinity = r.getString("vicinity");
                    iconURL = r.getString("icon");

                    if (pageFromDB < 0) { saveToDB(r); }

                    TableRow row = getTableRow(name, vicinity, iconURL);
                    table.addView(row);
                }
            }
            else if (tableFor == "favorites") {

                //todo: populate the table from the database
                int debug = 1;
            }

        }

        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

        return table;
    }

    private void saveToDB(JSONObject r) {

        try {

            String name = r.getString("name");
            String vicinity = r.getString("vicinity");
            String iconURL = r.getString("icon");
            String place_id = r.getString("place_id");
            int favorited = 0;

            Database db = new Database(activity);

            db.addEntry(place_id, name, vicinity, favorited, iconURL);
            Log.d("db", "saving to db..........................------------");
        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }


    }

    public TableLayout getDetailsInfoTable(TableLayout table, String response, boolean fromDB) {

        JSONObject results = null;
        Database db = new Database(activity);

        if (fromDB) {
            // response == place_id
            results = db.getDetailsInfo(response);
        }
        else {
            // save the new info to db
            try {

                JSONObject responseJSON = new JSONObject(response);
                results = responseJSON.getJSONObject("results");

            }
            catch(Exception e){
                // TODO: output no results/failed to get results error here
                Log.d("error", e.toString());
            }
        }

//        TableLayout table = new TableLayout(activity);
        String address, phoneNumber, priceLevel, googlePage, website, rating;

        try {

            address = results.getString("formatted_address");
            phoneNumber = results.getString("formatted_phone_number");
            priceLevel = results.getString("price_level");
            rating = results.getString("rating");
            googlePage = results.getString("url");
            website = results.getString("website");

            table.addView(getDetailsRow(address, "Address", false, false));
            table.addView(getDetailsRow(phoneNumber, "Phone Number", false, true));
            table.addView(getDetailsRow(priceLevel, "Price Level", false, false));
            table.addView(getDetailsRow(rating, "Rating", false, false));
            table.addView(getDetailsRow(googlePage, "Google Page", true, false));
            table.addView(getDetailsRow(website, "Website", true, false));


        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }

        return table;



    }

    private TableRow getDetailsRow(String data, String rowFor, boolean isURL, boolean isPhone) {

        TableRow row = new TableRow(activity);
        TextView left = new TextView(activity);
        RatingBar rating = new RatingBar(activity);
        RelativeLayout relativeLayout = new RelativeLayout(activity);

        if (rowFor.equals("Rating")) {

            rating.setNumStars(5);
            rating.setStepSize(0.1f);
            rating.setRating(3.6f);
            rating.setScaleX(0.6f);
            rating.setScaleY(0.6f);

            relativeLayout.addView(rating);
            relativeLayout.setPadding(-180, -50, 0, 0);

            // set the color of the stars
            LayerDrawable drawable = (LayerDrawable) rating.getProgressDrawable();
            drawable.getDrawable(2).setColorFilter((activity.getResources().getColor(R.color.webtech_pink)),
                    PorterDuff.Mode.SRC_ATOP);
        }
        TextView right = new TextView(activity);

        String url = "";

        if (isURL) {

            url = "<a href=\"" + data + "\">" + data + "</a>";
            data = url;
        }

        String leftStr = "<strong>" + rowFor + "</strong>";



        left.setText(Html.fromHtml(leftStr));
        right.setText(Html.fromHtml(data));
        left.setPadding(0, 3, 50, 3);
        left.setTextSize(15);
        right.setMaxWidth(1000);
        right.setPadding(0,0, 50, 0);

        // make URLs clickable and phone numbers callable
        right.setMovementMethod(LinkMovementMethod.getInstance());
        right.setLinkTextColor(activity.getResources().getColor(R.color.webtech_pink));
        right.setTextSize(15);
        if (isPhone) {
            Linkify.addLinks(right, Linkify.PHONE_NUMBERS);
        }
        else if (isURL) {

            Linkify.addLinks(right, Linkify.WEB_URLS);
        }

        row.addView(left);
        if (rowFor.equals("Rating")) {

            row.addView(relativeLayout);
            row.setPadding(50, 10, 10, 0);
        }
        else {
            row.addView(right);
            row.setPadding(50, 10, 10, 50);
        }

        return row;

    }

    public void populateReviews(TableLayout table, String source, boolean fromDB, SortBy sortBy) {

        String placeID = getPlaceID();
        try {

                JSONArray reviews = new JSONArray();


                if (fromDB) {

                    // just populate the table
                    Database db = new Database(activity);
                    reviews = db.getSortedReviews(placeID, source, sortBy);



                }
                else {

                    String key = "google_reviews_" + placeID;
                    if (source.equals("Yelp")) {

                        key = "yelp_reviews_" + placeID;
                    }
                }

                //loop here; if not fromDB, save it to db



            }
            catch(Exception e){
                // TODO: output no results/failed to get results error here
                Log.d("error", e.toString());
            }

    }

//    private JSONArray getReviewsFromDB(String placeID, String reviewSource, SortBy sortBy) {
//
//       Database db = new Database(activity);
//       JSONArray reviews = db.getSortedReviews(placeID, reviewSource, sortBy);
//
//
//
//
//    }

    private String getPlaceID() {

        String placeID = "ChIJfWmJOsfSD4gRVMPC4R4Q10w";

        return placeID;
    }
}