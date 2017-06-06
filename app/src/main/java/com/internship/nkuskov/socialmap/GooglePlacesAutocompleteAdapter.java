package com.internship.nkuskov.socialmap;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyCvEH7XUIJ4ui2mSXqLT4_l1ARgxTNsWSs";

    private ArrayList<String> mResultList;

    public GooglePlacesAutocompleteAdapter(@NonNull Context context, @LayoutRes int textViewResourceID) {
        super(context, textViewResourceID);
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {

                    //Retrieve the autocomplete results
                    mResultList = autocomplete(constraint.toString());

                    //Assign the data to the FilterResults
                    filterResults.values = mResultList;
                    filterResults.count = mResultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection httpURLConnection = null;
        StringBuilder jsonResult = new StringBuilder();

        try {
            //Buiild an URL inquiry
            StringBuilder stringBuilder = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            stringBuilder.append("?input=" + URLEncoder.encode(input, "utf8"));
            stringBuilder.append("&location=");
            stringBuilder.append(MapsActivity.getmLastLocation().getLatitude() + "," + MapsActivity.getmLastLocation().getLongitude());
            stringBuilder.append("&radius=500");
            stringBuilder.append("&key=" + API_KEY);

            URL url = new URL(stringBuilder.toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStream = new InputStreamReader(httpURLConnection.getInputStream());

            //Load a result into StringBuilder jsonResult
            int read;
            char[] buff = new char[1024];
            while ((read = inputStream.read(buff)) != -1) {
                jsonResult.append(buff, 0, read);
            }


        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        try {
            //Create JSONObject from URL connection
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("predictions");

            //Get Place description from JCON Object
            resultList = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                resultList.add(jsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
}
