package bizu.work.placessearch;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
        Table tableObj = new Table(activity, null, v, this);

        if (db.getCount("favorites") > 0) {


            TableLayout table = tableObj.populateTable("favorites", pageFromDB);
            table.setPadding(0,50, 0,50 );


        }
        else {

            tableObj.showEmpty("favorites");
        }
    }

}