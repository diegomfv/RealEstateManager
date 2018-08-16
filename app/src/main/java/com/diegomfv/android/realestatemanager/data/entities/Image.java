package com.diegomfv.android.realestatemanager.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Entity (tableName = "image")
public class Image {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @ColumnInfo(name = "firebase_storage_directory")
    private String firebaseStorageDirectory;

    @ColumnInfo (name = "internal_storage_directory")
    private String internalStorageDirectory;

    private String description;

    public Image(int id, String firebaseStorageDirectory, String internalStorageDirectory, String description) {
        this.id = id;
        this.firebaseStorageDirectory = firebaseStorageDirectory;
        this.internalStorageDirectory = internalStorageDirectory;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseStorageDirectory() {
        return firebaseStorageDirectory;
    }

    public void setFirebaseStorageDirectory(String firebaseStorageDirectory) {
        this.firebaseStorageDirectory = firebaseStorageDirectory;
    }

    public String getInternalStorageDirectory() {
        return internalStorageDirectory;
    }

    public void setInternalStorageDirectory(String internalStorageDirectory) {
        this.internalStorageDirectory = internalStorageDirectory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", firebaseStorageDirectory='" + firebaseStorageDirectory + '\'' +
                ", internalStorageDirectory='" + internalStorageDirectory + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
