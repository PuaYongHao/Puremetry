package com.example.puremetry;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;

public class Profile implements Parcelable {

    private int profileID;
    private String name;
    private Gender gender;
    private Date dateOfBirth;
    private String nric;

    public Profile(int profileID, String name, Gender gender, Date dateOfBirth, String nric) {
        this.profileID = profileID;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.nric = nric;
    }

    public Profile(Parcel source) {
        try {
            profileID = source.readInt();
            name = source.readString();
            gender = Gender.valueOf(source.readString());
            dateOfBirth = ProfilesListController.DATE_FORMAT.parse(source.readString());
            nric = source.readString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(profileID);
        dest.writeString(name);
        dest.writeString(gender.name());
        dest.writeString(ProfilesListController.DATE_FORMAT.format(dateOfBirth.getTime()));
        dest.writeString(nric);
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

}
