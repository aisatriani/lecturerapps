package com.tenilodev.lecturermaps.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by azisa on 1/10/2017.
 */

public class LokasiDosen implements Serializable, Parcelable {

    private int id;
    private String nidn;
    private String nama;
    private double latitude;
    private double longitude;
    private int active;
    private String created_at;
    private String updated_at;

    protected LokasiDosen(Parcel in) {
        id = in.readInt();
        nidn = in.readString();
        nama = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        active = in.readInt();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<LokasiDosen> CREATOR = new Creator<LokasiDosen>() {
        @Override
        public LokasiDosen createFromParcel(Parcel in) {
            return new LokasiDosen(in);
        }

        @Override
        public LokasiDosen[] newArray(int size) {
            return new LokasiDosen[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNidn() {
        return nidn;
    }

    public void setNidn(String nidn) {
        this.nidn = nidn;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nidn);
        dest.writeString(nama);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(active);
        dest.writeString(created_at);
        dest.writeString(updated_at);
    }
}
