package com.android.mapsampleapplication.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;

import com.google.gson.annotations.SerializedName;

public class Vehicle implements Parcelable {

    private String id;
    @SerializedName("fleetType")
    private String type;
    @SerializedName("heading")
    private String dest;

    protected Vehicle(Parcel in) {
        id = in.readString();
        type = in.readString();
        dest = in.readString();
        coordinate = in.readParcelable(Coordinate.class.getClassLoader());
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    private Coordinate coordinate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(type);
        parcel.writeString(dest);
        parcel.writeParcelable(coordinate,flags);
    }
}
