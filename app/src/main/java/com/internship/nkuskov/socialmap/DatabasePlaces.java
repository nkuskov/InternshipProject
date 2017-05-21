package com.internship.nkuskov.socialmap;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nkuskov on 5/19/2017.
 */

public class DatabasePlaces {
    public String location;
    public List<Integer> statistic;

    public DatabasePlaces(){
        //Needed for FireBase DataBase
    }

    public DatabasePlaces(String location, List<Integer> statistic){
        this.location = location;
        this.statistic = statistic;
        this.statistic.add(0);
    }


    /**
     * Created by Admin on 14.05.2017.
     */

    public static class RegularPayment implements Parcelable {
        public boolean checked;
        public String name;
        public Long date;
        public Long id_reg_payment;

        public RegularPayment() {
            // Needed for Firebase
        }

        public RegularPayment(String name, Long date) {
            this.name = name;
            this.date = date;
            this.id_reg_payment = System.nanoTime();
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getDate() {
            return date;
        }

        public void setDate(Long date) {
            this.date = date;
        }

        public Long getId_reg_payment() {
            return id_reg_payment;
        }

        public void setId_reg_payment(Long id_reg_payment) {
            this.id_reg_payment = id_reg_payment;
        }

        //parcel part
        public RegularPayment(Parcel in) {
            this.name = in.readString();
            this.date = in.readLong();
            this.id_reg_payment = in.readLong();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeLong(date);
            dest.writeLong(id_reg_payment);
        }

        public static final Creator<RegularPayment> CREATOR = new Creator<RegularPayment>() {

            @Override
            public RegularPayment createFromParcel(Parcel source) {
                return new RegularPayment(source); //using parcelable constructor
            }

            @Override
            public RegularPayment[] newArray(int size) {
                return new RegularPayment[size];
            }
        };
    }
}
