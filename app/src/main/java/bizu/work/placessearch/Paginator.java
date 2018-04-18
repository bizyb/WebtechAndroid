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

    public void showPagination(int pageNum) {

        RelativeLayout paginator = activity.findViewById(R.id.pagination_container);
        paginator.removeAllViews();
        activity.getLayoutInflater().inflate(R.layout.pagination,  paginator, true);

        // handle prev button
        //TODO: set listener if previous page available and make it clickable
        final Button prevBtn = (Button) activity.findViewById(R.id.btn_prev);
        final Button nextBtn = (Button) activity.findViewById(R.id.btn_next);

        nextBtn.setEnabled(false);
        nextBtn.setClickable(false);

        if (pageNum == 1) {

            prevBtn.setEnabled(false);
            prevBtn.setClickable(false);
        }
        else {
            setPrevBtnListener(prevBtn, pageNum);
        }

        try {

            JSONObject r = new JSONObject(response);
            String next_page_token = r.getString("next_page_token");

            if (next_page_token != null && (next_page_token.length() > 1)) {

                //If there is a next page token, make a new request to the api

                nextBtn.setEnabled(true);
                nextBtn.setClickable(true);

                nextBtn.setTag(next_page_token);
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        resultsInstace.loadPaginatedPage(null, nextBtn);
                    }
                });

            }
//            else if (next_page_token != null) {
//
//                // a next page token
//            }
        }
        catch(Exception e){
            // TODO: output no results/failed to get results error here
            Log.d("error", e.toString());
        }


    }

    private void setPrevBtnListener(final Button prevBtn, int currentPage) {

        prevBtn.setTag(currentPage);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultsInstace.loadPaginatedPage(prevBtn, null);
            }
        });


    }
}
