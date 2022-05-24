package com.captivepet.sweardle.ui.main;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;

import com.captivepet.sweardle.CustomImageButton;
import com.captivepet.sweardle.TilePair;

public class MainViewModel extends ViewModel {

    private static final String TAG = "SWEARBUG";
    public static final char[] KEY_LABEL = new char[]{'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            '↵', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '←'};

    final static int ENTER = '↵';
    final static int DEL = '←';
    private MutableLiveData<String> signal;
    public MutableLiveData<String> getSignal() {
        if (signal == null) {
            signal = new MutableLiveData<String>();
        }
        return signal;
    }
    private String gameWord;
    private int rowsDone = 0;
    private boolean TESTED = false;
    public boolean getTESTED () {
        return TESTED;
    };
    public void setTESTED (boolean b) {
        TESTED = b;
    }
    private boolean WINNER = false;
    private ArrayList<TilePair> currentRow = new ArrayList<>();
    public ArrayList<TilePair> getCurrentRow() { return currentRow; }
    public void clearCurrentRow() { currentRow.clear(); }

    private String getWord() {
        return gameWord;
    }

    public void setGameWord(String gameWord) {
        this.gameWord = gameWord;
    }

    void init(String[] dict) {
        gameWord = dict[(int) (Math.random() * dict.length)];
    }

    public void onClick(View view) {
        char keyChar;
        int ix;
        int position = currentRow.size();
        if (view instanceof CustomImageButton) {
            keyChar = ((CustomImageButton) view).getText().charAt(0);
        }else{
            keyChar = ((Button) view).getText().charAt(0);
        }
        Log.d(TAG, String.format(Locale.US, "keytap: %c", keyChar));
        if (keyChar == ENTER && !TESTED && position == GameFragment.WORD_LENGTH) {
            WINNER = testWord();
            TESTED = true;
        } else if (keyChar == DEL && !TESTED && position > 0) {
            currentRow.remove(position - 1);
        } else if (position < GameFragment.WORD_LENGTH) { // letter key
            currentRow.add(new TilePair(keyChar, TilePair.UNCHECKED));
        }
        signal.setValue("signal");
    }

    public void newRow() {
        rowsDone++;
        currentRow.clear();
        TESTED = false;
    }

    private boolean testWord() {
        boolean WIN = true;
        if (currentRow == null) {
            return false;
        }
        int position = currentRow.size();
        if (position > 0 && position % GameFragment.WORD_LENGTH == 0) {
            TilePair[] guess = new TilePair[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix ++) {
                guess[ix] = currentRow.get(position - GameFragment.WORD_LENGTH + ix);
            }
            char[] word = gameWord.toCharArray();
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                    if (word[ix] == guess[jx].getC()) {
                        if (ix == jx) {
                            guess[jx].setD(TilePair.CORRECT);
                            break;
                        } else if (guess[jx].getD() == TilePair.UNCHECKED) {
                            guess[jx].setD(TilePair.MISPLACED);
                            WIN = false;
                            break;
                        }
                    }
                }
            }
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (guess[jx].getD() == TilePair.UNCHECKED) {
                    guess[jx].setD(TilePair.INCORRECT);
                    WIN = false;
                }
            }
        }
        return WIN;
    }
    public int getRowsDone() {
        return rowsDone;
    }
}