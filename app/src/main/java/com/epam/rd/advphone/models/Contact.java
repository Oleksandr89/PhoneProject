package com.epam.rd.advphone.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_EMAIL;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_FAVOURITE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_ID;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_IMAGE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_NAME;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_PHONE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_TYPE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.DB_NAME;

@Entity(tableName = DB_NAME)
public class Contact implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CONTACT_ID)
    private int id;

    @ColumnInfo(name = CONTACT_IMAGE)
    private String contactImage;

    @ColumnInfo(name = CONTACT_NAME)
    private String name;

    @ColumnInfo(name = CONTACT_FAVOURITE)
    private boolean favourite;

    @ColumnInfo(name = CONTACT_PHONE)
    private String phone;

    @ColumnInfo(name = CONTACT_TYPE)
    private String type;

    @ColumnInfo(name = CONTACT_EMAIL)
    private String email;

    public Contact() {
    }

    @Ignore
    public Contact(int id, String contactImage, String name, String phone, String type, String email) {
        this.id = id;
        this.contactImage = contactImage;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.contactImage);
        dest.writeString(this.name);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeString(this.phone);
        dest.writeString(this.type);
        dest.writeString(this.email);
    }

    protected Contact(Parcel in) {
        this.id = in.readInt();
        this.contactImage = in.readString();
        this.name = in.readString();
        this.favourite = in.readByte() != 0;
        this.phone = in.readString();
        this.type = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
