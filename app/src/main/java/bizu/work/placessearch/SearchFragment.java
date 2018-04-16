package bizu.work.placessearch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;


public class SearchFragment extends Fragment {


    public SearchFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Populate the dropdown list and inflate the layout fragment
        View v = inflater.inflate(R.layout.search_form, container, false);
        populateDropdown(v);

        // Set button onclick listeners
        Button btnClear = (Button) v.findViewById(R.id.btn_clear);
        Button btnSearch = (Button) v.findViewById(R.id.btn_search);
        RadioButton otherLocBtn = (RadioButton) v.findViewById(R.id.radio_other_loc);
        RadioButton currLocBtn = (RadioButton) v.findViewById(R.id.radio_current_loc);

        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                clearForm(v);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                search();
            }
        });

        currLocBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateRadioBtn(v);
            }
        });

        otherLocBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                updateRadioBtn(v);
            }
        });




        return v;
    }

    public void populateDropdown(View v) {

        String [] values =  getResources().getStringArray(R.array.categories);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);

        if (spinner == null) {spinner = getActivity().findViewById(R.id.spinner);}

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

    }


    private void updateRadioBtn(View v) {

        // Toggle button coloration. If otherLocBtn is checked, enable the input box. Otherwise,
        // disable the box.

        RadioButton currLocBtn = (RadioButton) getActivity().findViewById(R.id.radio_current_loc);
        RadioButton otherLocBtn = (RadioButton) getActivity().findViewById(R.id.radio_other_loc);
        EditText otherLocInput = (EditText) getActivity().findViewById(R.id.other_loc_input);

        if (currLocBtn.isChecked()) {

            resetRadioBtn();
        }
        else if (otherLocBtn.isChecked()) {

            currLocBtn.setButtonTintList(getResources().getColorStateList(R.color.radioInactive));
            otherLocBtn.setButtonTintList(getResources().getColorStateList(R.color.webtech_pink));
            otherLocInput.setInputType(InputType.TYPE_CLASS_TEXT);
            otherLocInput.setFocusableInTouchMode(true);
            otherLocInput.setFocusable(true);

        }
    }

    private void resetRadioBtn() {

        // Reset radio buttons and disable custom location input

        RadioButton currLocBtn = (RadioButton) getActivity().findViewById(R.id.radio_current_loc);
        RadioButton otherLocBtn = (RadioButton) getActivity().findViewById(R.id.radio_other_loc);
        EditText otherLocInput = (EditText) getActivity().findViewById(R.id.other_loc_input);

        currLocBtn.setButtonTintList(getResources().getColorStateList(R.color.webtech_pink));
        otherLocBtn.setButtonTintList(getResources().getColorStateList(R.color.radioInactive));
        otherLocInput.setInputType(InputType.TYPE_NULL);
        otherLocInput.setFocusableInTouchMode(false);
        otherLocInput.setFocusable(false);

    }
    private void validateForm() {


    }

    private void search() {


    }
    private void clearForm(View v) {

        EditText keyword = (EditText) getActivity().findViewById(R.id.keyword_input);;
        EditText distance = (EditText) getActivity().findViewById((R.id.distance_input));
        EditText otherLocInput = (EditText) getActivity().findViewById(R.id.other_loc_input);

        keyword.setText("");
        distance.setText("");
        otherLocInput.setText("");
        populateDropdown(v);
        resetRadioBtn();

    }







}