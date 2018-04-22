package bizu.work.placessearch;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import db.Database;


public class FavoritesFragment extends Fragment  {

    private Activity activity;
    public FavoritesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v =  inflater.inflate(R.layout.favorites_fragment, container, false);
        activity = getActivity();


        populateFavorites(v, 1);
        return v;
    }


    public void populateFavorites(View v, int pageFromDB) {


        Database db = new Database(activity);
        if (db.getCount("favorites") > 0) {

            TextView textView = v.findViewById(R.id.favorites_no_favs);
            textView.setText("");

            Table tableObj = new Table(activity, null, v, this);
            TableLayout table = tableObj.populateTable("favorites", pageFromDB);

//            Paginator paginator = new Paginator(activity, null, null, this);

            getLayoutInflater().inflate(R.layout.table_layout, table, true);
            table.setPadding(0,50, 0,50 );
//            if (pageFromDB > 0) {
//                paginator.showPagination(pageFromDB);
//            }
//            else {
//                paginator.showPagination(pageFromDB);
//            }

        }
        else {

            Log.i("in fav", "---------------------------in fav----");
            getLayoutInflater().inflate(R.layout.favorites_fragment, null);
        }
    }

}