package com.internship.nkuskov.socialmap.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.internship.nkuskov.socialmap.GooglePlacesAutocompleteAdapter;
import com.internship.nkuskov.socialmap.R;


public class AddNewDestinationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "AddNewDestinationFragment TAG";
    private Button closeButton;
    private View mView;
    private AutoCompleteTextView mAutoCompleteTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.add_destination_fragment, null);
        mAutoCompleteTextView = (AutoCompleteTextView)mView.findViewById(R.id.auto_complete_text_view);
        mAutoCompleteTextView.setAdapter(new GooglePlacesAutocompleteAdapter(mView.getContext(),R.layout.autocomplete_list_item));
        mAutoCompleteTextView.setOnItemClickListener(this);
        closeButton = (Button) mView.findViewById(R.id.close_fragment_btn);
        closeButton.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_fragment_btn:
                getFragmentManager().beginTransaction().remove(this).commit();
                break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
