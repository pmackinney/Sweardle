package com.captivepet.sweardle;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.captivepet.sweardle.ui.main.GameFragment;

import java.security.InvalidParameterException;
import java.util.Locale;

// https://alvinalexander.com/java/java-tuple-classes/
public class TilePair implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TilePair createFromParcel(Parcel in) {
            return new TilePair(in);
        }
        public TilePair[] newArray(int size) {
            return new TilePair[size];
        }
    };

    // fields
    private byte keyCharacter;
    private int statusId;

    // status IDs associate status with color of key or tile
    public final static int UNCHECKED = R.drawable.frame_white;
    public final static int INCORRECT = R.drawable.frame_grey;
    public final static int CORRECT = R.drawable.frame_green;
    public final static int MISPLACED = R.drawable.frame_ochre;

    // constructor
    public TilePair(char keyCharacter, int statusId) {
        set(keyCharacter, statusId);
    }

    public char getChar() {
        return (char) keyCharacter;
    }

    public int getStatusId() {
        return statusId;
    }

    public void set(int statusId) throws InvalidParameterException {
        switch(statusId) {
            case INCORRECT:
                break;
            case CORRECT:
                break;
            case MISPLACED:
                break;
            case UNCHECKED:
                break;
            default:
                throw new InvalidParameterException();
        }
        this.statusId = statusId;
    }

    public void set(char keyCharacter, int statusId) throws InvalidParameterException {
        if ('A' <= keyCharacter && keyCharacter <= 'Z') {
            this.keyCharacter = (byte) keyCharacter;
            this.statusId = statusId;
        } else {
            throw new InvalidParameterException();
        }
    }

    // Parcelling part
    public TilePair(Parcel in){
        this.keyCharacter = in.readByte();
        this.statusId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.keyCharacter);
        dest.writeInt(this.statusId);
    }

    @NonNull
    @Override
    public String toString() {
        String status;
        switch(statusId) {
            case INCORRECT: status = "INCORRECT"; break;
            case CORRECT: status = "CORRECT"; break;
            case MISPLACED: status = "MISPLACED"; break;
            default: status = "UNCHECKED";
        }
        return String.format(Locale.US, "%b %s", keyCharacter, status);
    }
}
