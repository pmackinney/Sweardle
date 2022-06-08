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

    public TilePair() {
        c = GameFragment.EMPTY;
        d = UNCHECKED;
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
