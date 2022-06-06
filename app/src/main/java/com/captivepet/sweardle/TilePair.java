package com.captivepet.sweardle;

// https://alvinalexander.com/java/java-tuple-classes/
public class TilePair {
    private char keyCharacter;
    private int statusId;

    // status IDs associate status with color of key or tile
    public final static int UNCHECKED = R.drawable.frame_white;
    public final static int INCORRECT = R.drawable.frame_grey;
    public final static int CORRECT = R.drawable.frame_green;
    public final static int MISPLACED = R.drawable.frame_ochre;

    public TilePair(char keyCharacter, int statusId) {
        set(keyCharacter, statusId);
    }

    public char getChar() {
        return keyCharacter;
    }

    public int getStatusId() {
        return statusId;
    }

    public void set(int statusId) {
        this.statusId = statusId;
    }

    public void set(char keyCharacter, int statusId) {
        this.keyCharacter = keyCharacter;
        this.statusId = statusId;
    }
}
