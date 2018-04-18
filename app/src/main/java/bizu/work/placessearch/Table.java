package bizu.work.placessearch;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

//    public int getPageNum() {
//
//        return pageNum;
//    }

}
