package com.diegomfv.android.realestatemanager.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 06/10/2018.
 */

@Entity(tableName = "agent")
public class Agent implements Parcelable {

    @PrimaryKey
    @NonNull
    private String email;

    private String password;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "memorable_data_question")
    private String memorableDataQuestion;

    @ColumnInfo(name = "memorable_data_answer")
    private String memorableDataAnswer;

    public Agent(@NonNull String email, String password, String firstName, String lastName, String memorableDataQuestion, String memorableDataAnswer) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.memorableDataQuestion = memorableDataQuestion;
        this.memorableDataAnswer = memorableDataAnswer;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMemorableDataQuestion() {
        return memorableDataQuestion;
    }

    public void setMemorableDataQuestion(String memorableDataQuestion) {
        this.memorableDataQuestion = memorableDataQuestion;
    }

    public String getMemorableDataAnswer() {
        return memorableDataAnswer;
    }

    public void setMemorableDataAnswer(String memorableDataAnswer) {
        this.memorableDataAnswer = memorableDataAnswer;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", memorableDataQuestion='" + memorableDataQuestion + '\'' +
                ", memorableDataAnswer='" + memorableDataAnswer + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.memorableDataQuestion);
        dest.writeString(this.memorableDataAnswer);
    }

    protected Agent(Parcel in) {
        this.email = in.readString();
        this.password = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.memorableDataQuestion = in.readString();
        this.memorableDataAnswer = in.readString();
    }

    public static final Creator<Agent> CREATOR = new Creator<Agent>() {
        @Override
        public Agent createFromParcel(Parcel source) {
            return new Agent(source);
        }

        @Override
        public Agent[] newArray(int size) {
            return new Agent[size];
        }
    };
}
