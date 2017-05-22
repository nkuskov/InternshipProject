package com.internship.nkuskov.socialmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DestinationList extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    DatabaseReference myRef;
    FirebaseListAdapter mAdapter;

    private EditText ETnewDestionation;

    ListView destList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_list);
        myRef = FirebaseDatabase.getInstance().getReference();

        destList = (ListView) findViewById(R.id.discr_for_task);


        mAdapter = new FirebaseListAdapter<String>(this, String.class, android.R.layout.simple_list_item_1, myRef.child(user.getUid()).child("DestList")) {
            @Override
            protected void populateView(View v, String model, int position) {

                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(model);
            }


        };

        destList.setAdapter(mAdapter);

    }
}
