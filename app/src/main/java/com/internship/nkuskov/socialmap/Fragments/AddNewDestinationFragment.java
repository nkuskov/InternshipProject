package com.internship.nkuskov.socialmap.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.internship.nkuskov.socialmap.MapsActivity;
import com.internship.nkuskov.socialmap.PlacesAutoCompleteAdapter;
import com.internship.nkuskov.socialmap.R;


public class AddNewDestinationFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "AddNewDestinationFragment TAG";
    private Button closeButton;
    private Button addButton;
    private View mView;
    private RecyclerView destPlaceList;
    private PlacesAutoCompleteAdapter mPlacesAutoCompleteAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private EditText destPlaceText;
    private EditText destNameEditText;
    private GridView destIconGridView;
    private Integer destIconId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.add_destination_fragment, null);

        destIconGridView = (GridView) mView.findViewById(R.id.dest_icon_grid);
        destIconGridView.setAdapter(new GridIconAdapter(mView.getContext()));
        initGridViewOnItemClick();

        mPlacesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(mView.getContext(), R.layout.autocomplete_list_item, MapsActivity.mGoogleApiClient, null);
        destPlaceList = (RecyclerView) mView.findViewById(R.id.dest_place_list);
        mLinearLayoutManager = new LinearLayoutManager(mView.getContext());
        destPlaceList.setLayoutManager(mLinearLayoutManager);
        destPlaceList.setAdapter(mPlacesAutoCompleteAdapter);
        destPlaceText = (EditText) mView.findViewById(R.id.auto_complete_text_view);
        destNameEditText = (EditText) mView.findViewById(R.id.dest_name_text);

        destPlaceText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlacesAutoCompleteAdapter.getFilter().filter(s.toString());
                destPlaceList.bringToFront();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    mPlacesAutoCompleteAdapter.getFilter().filter(s.toString());
                }
            }
        });



        addButton = (Button) mView.findViewById(R.id.add_new_destination_btn);
        addButton.setOnClickListener(this);
        closeButton = (Button) mView.findViewById(R.id.close_fragment_btn);
        closeButton.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_fragment_btn:
                getFragmentManager().beginTransaction().remove(this).commit();
                destPlaceText.setText("");
                destNameEditText.setText("");
                destIconId = null;
                break;
            case R.id.add_new_destination_btn:
                if (destIconId == null || destPlaceText.getText().length() == 0 || destNameEditText.getText().length() == 0) {
                    Toast.makeText(mView.getContext(), "Write correct parameters", Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    public void initGridViewOnItemClick() {
        destIconGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                destIconId = (Integer) view.getTag();
            }
        });
    }

//    public void recyclerViewOnItemCallback(){
//        destPlaceList.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
//                        final String placeId = String.valueOf(item.placeId);
//                        Log.i("TAG", "Autocomplete item selected: " + item.description);
//                        /*
//                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
//                         */
//
//                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                                .getPlaceById(mGoogleApiClient, placeId);
//                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                            @Override
//                            public void onResult(PlaceBuffer places) {
//                                if(places.getCount()==1){
//                                    //Do the things here on Click.....
//                                    Toast.makeText(getApplicationContext(),String.valueOf(places.get(0).getLatLng()),Toast.LENGTH_SHORT).show();
//                                }else {
//                                    Toast.makeText(getApplicationContext(),Constants.SOMETHING_WENT_WRONG,Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        Log.i("TAG", "Clicked: " + item.description);
//                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
//                    }
//                })
//        );
//    }

}
