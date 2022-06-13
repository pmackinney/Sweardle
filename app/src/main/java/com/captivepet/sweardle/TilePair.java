package com.captivepet.sweardle;

import com.captivepet.sweardle.ui.main.GameFragment;

import java.util.ArrayList;

import kotlin.UNumbersKt;

// https://alvinalexander.com/java/java-tuple-classes/
public class TilePair {
    private char c;
    private int d;

    public final static int UNCHECKED = R.drawable.frame_white;
    public final static int INCORRECT = R.drawable.frame_grey;
    public final static int CORRECT = R.drawable.frame_green;
    public final static int MISPLACED = R.drawable.frame_ochre; //0824

    /**
     * for debugging
     * @param r - the resource ID
     * @return A status name
     */
    public static String getStatusName(int r) {
        switch(r) {
            case UNCHECKED: return "UNCHECKED";
            case INCORRECT: return "INCORRECT";
            case CORRECT: return "CORRECT";
            case MISPLACED: return "MISPLACED";
            default: return "BUMMER";
        }
    }

    public TilePair() {
        setPair(GameFragment.EMPTY, UNCHECKED);
    }

    public TilePair(char c, int d) {
        setPair(c, d);
    }

    public char getChar() {
        return c;
    }

    public int getStatus() {
        return d;
    }

    public void setStatus(int d) {
        if (d == UNCHECKED || d == INCORRECT || d == CORRECT || d == MISPLACED) {
            this.d = d;
        }
    }

    public void setPair(char c, int d) {
        this.c = c;
        this.d = d;
    }
}
