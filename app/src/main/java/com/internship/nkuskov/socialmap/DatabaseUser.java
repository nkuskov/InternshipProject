package com.internship.nkuskov.socialmap;

import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by nkuskov on 5/20/2017.
 */

public class DatabaseUser {
    ArrayList<String> accounts;
    DatabaseLocation currentLocation;


    public DatabaseUser(){

    }

    public DatabaseUser(ArrayList<String> accounts, DatabaseLocation currentLocation){
        this.accounts =accounts;
        accounts.add("");
        this.currentLocation = currentLocation;
    }
}
