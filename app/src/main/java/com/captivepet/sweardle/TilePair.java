package com.captivepet.sweardle;
import com.captivepet.sweardle.R;

// https://alvinalexander.com/java/java-tuple-classes/
public class TilePair {
    private char c;
    private int d;

    public final static int UNCHECKED = R.drawable.frame_white;
    public final static int INCORRECT = R.drawable.frame_grey;
    public final static int CORRECT = R.drawable.frame_green;
    public final static int MISPLACED = R.drawable.frame_ochre;

    public TilePair(char c, int d) {
        setPair(c, d);
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public void setPair(char c, int d) {
        this.c = c;
        this.d = d;
    }
}
