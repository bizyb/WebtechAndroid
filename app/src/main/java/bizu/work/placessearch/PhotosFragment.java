package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import db.Database;

public class PhotosFragment extends Fragment {

    private String placeID;
    private View v;

    public PhotosFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Populate the dropdown list and inflate the layout fragment
        v = inflater.inflate(R.layout.photos_fragment, container, false);

        DetailsActivity activity = (DetailsActivity) getActivity();
        placeID = activity.getDetailsPlaceID();

        populatePhotosTab(placeID);

        return v;
    }

    public void populatePhotosTab(String placeIDLocal) {

//        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.photos_container);
//        if (linearLayout == null) {
//            linearLayout = v.findViewById(R.id.photos_container);
//        }
//
//        Database db = new Database(getActivity());
//        ArrayList<String> photosArray = db.getDetailsPhotos(placeIDLocal);
//
//
//        for (String url : photosArray) {
//
//
//            ImageView image = new ImageView(getActivity());
//            image.setPadding(0, 0, 0, 50);
//
//
//            image.setAdjustViewBounds(true);
//            Picasso.get().load(url).into(image);
//            linearLayout.addView(image);
//
//        }

    }

}

