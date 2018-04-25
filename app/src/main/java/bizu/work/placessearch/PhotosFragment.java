package bizu.work.placessearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

        Log.d("db", "populatePhotosTab......................attempting to populate photos....--------------------------------PHOTO FUNC-----------: ");

        boolean hasPhotos = false;

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.photos_container);
//        if (linearLayout == null) {
//            linearLayout = v.findViewById(R.id.photos_container);
//        }

        Database db = new Database(getActivity());

        // todo: block here until the photo request finishes; set another flag in savePhotos in db to indicate if the request is complete
        // todo if request complete but array is null, then the place has not photos
        ArrayList<String> photosArray = null;
        photosArray = db.getDetailsPhotos(placeIDLocal);

//        while (photosArray == null ) {
//            photosArray = db.getDetailsPhotos(placeIDLocal);
//        }


        if (photosArray != null) {
            for (String url : photosArray) {


                ImageView image = new ImageView(getActivity());
                image.setPadding(0, 0, 0, 50);


                image.setAdjustViewBounds(true);
                Picasso.get().load(url).into(image);
                linearLayout.addView(image);

            }

        }
        else {

            new Table(getActivity(), null, v, null).showEmpty("photos");
        }
    }

}

