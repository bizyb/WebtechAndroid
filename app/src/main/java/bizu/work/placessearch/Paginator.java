package bizu.work.placessearch;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import org.json.JSONObject;

public class Paginator {

    private Activity activity;
    private String response;
    private ResultsActivity resultsInstace;
//    private View view;

    public Paginator(Activity activity, String response, ResultsActivity resultsInstace) {

        this.activity = activity;
        this.response = response;
        this.resultsInstace = resultsInstace;

    }

    public void showPagination() {

        RelativeLayout paginator = activity.findViewById(R.id.pagination_container);
        paginator.removeAllViews();
        activity.getLayoutInflater().inflate(R.layout.pagination,  paginator, true);

        // handle prev button
        //TODO: set listener if previous page available and make it clickable
        Button prevBtn = (Button) activity.findViewById(R.id.btn_prev);
        prevBtn.setEnabled(false);
        prevBtn.setClickable(false);


        Button nextBtn = (Button) activity.findViewById(R.id.btn_next);
        nextBtn.setEnabled(false);
        nextBtn.setClickable(false);

        try {

            JSONObject r = new JSONObject(response);
            String next_page_token = r.getString("next_page_token");

            if (next_page_token != null) {

                nextBtn.setEnabled(true);
                nextBtn.setClickable(true);

                nextBtn.setTag(next_page_token);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        resultsInstace.loadPaginatePage();
                    }
                });

            }
        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }


    }
}
