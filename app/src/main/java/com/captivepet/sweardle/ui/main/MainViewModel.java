package com.captivepet.sweardle.ui.main;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.security.Key;
import java.util.ArrayList;
import java.util.Locale;

import com.captivepet.sweardle.CustomImageButton;
import com.captivepet.sweardle.TilePair;

public class MainViewModel extends ViewModel {

    private static final String TAG = "SWEARBUG";

    final static int ENTER = '↵';
    final static int DEL = '←';
    private MutableLiveData<String> keyboardSignal;
    public MutableLiveData<String> getKeyboardSignal() {
        if (keyboardSignal == null) {
            keyboardSignal = new MutableLiveData<String>();
        }
        return keyboardSignal;
    }
private MutableLiveData<String> gameSignal;
    public MutableLiveData<String> getGameSignal() {
        if (gameSignal == null) {
            gameSignal = new MutableLiveData<String>();
        }
        return gameSignal;
    }
    private String gameWord;
    private int rowsDone = 0;
    private boolean TESTED = false;
    public boolean getTESTED () {
        return TESTED;
    }
    public void setTESTED (boolean b) {
        TESTED = b;
    }
    private boolean WINNER = false;
    final private ArrayList<TilePair> currentRow = new ArrayList<>();
    public ArrayList<TilePair> getCurrentRow() { return currentRow; }
    public void clearCurrentRow() { currentRow.clear(); }
    private String[] dict;

    private String getWord() {
        return gameWord;
    }

    public void setGameWord(String gameWord) {
        this.gameWord = gameWord;
    }

    void init(String[] dict) {
        this.dict = dict;
        gameWord = dict[(int) (Math.random() * dict.length)];
    }

    public void onClick(View view) {
        if (WINNER) {
            return;
        }
        char keyChar;
        int position = currentRow.size();
        if (view instanceof CustomImageButton) {
            keyChar = ((CustomImageButton) view).getText().charAt(0);
        } else {
            keyChar = ((Button) view).getText().charAt(0);
        }
        Log.d(TAG, String.format(Locale.US, "keytap: %c", keyChar));
        if (keyChar == ENTER && !TESTED && position == GameFragment.WORD_LENGTH) {
            if (validateGuess()) {
                WINNER = testWord();
                TESTED = true;
                getKeyboardSignal().setValue(GameFragment.ROW_UPDATED);
            }
        } else if (keyChar == DEL && !TESTED && position > 0) {
            currentRow.remove(position - 1);
        } else if (keyChar == ENTER || keyChar == DEL) {
            return;
        } else if (position < GameFragment.WORD_LENGTH) { // letter key
            currentRow.add(new TilePair(keyChar, TilePair.UNCHECKED));
        }
        if (WINNER) {
            gameSignal.setValue(GameFragment.WINNER);
        } else {
            gameSignal.setValue(GameFragment.READY);
        }
    }

    public void newRow() {
        rowsDone++;
        currentRow.clear();
        TESTED = false;
    }

    private boolean validateGuess() {
        int position = currentRow.size();
        if (position > 0 && position % GameFragment.WORD_LENGTH == 0) {
            char[] guessWord = new char[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                guessWord[ix] = currentRow.get(position - GameFragment.WORD_LENGTH + ix).getC();
            }
            boolean VALID = false;
            for (String s : dict) {
                if (s.equals(String.valueOf(guessWord))) {
                    VALID = true;
                    break;
                }
            }
            if (VALID) {
                return true;
            }
        }
        gameSignal.setValue("Bad word");
        return false;
    }

    private boolean testWord() {
        TilePair[] guess = new TilePair[GameFragment.WORD_LENGTH];
        char[] word = gameWord.toCharArray();
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find correct
            guess[ix] = currentRow.get(currentRow.size() - GameFragment.WORD_LENGTH + ix);
            if (word[ix] == guess[ix].getC()) {
                guess[ix].setD(TilePair.CORRECT);
                word[ix] = GameFragment.EMPTY;
            }
        }
        boolean WIN = true;
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find misplaced
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (word[ix] == GameFragment.EMPTY) {
                    continue; // already matched
                } else if (word[ix] == guess[jx].getC() && guess[jx].getD() == TilePair.UNCHECKED) {
                    guess[jx].setD(TilePair.MISPLACED);
                    WIN = false;
                    break; // word[ix] only gets one match
                }
            }
        }
        for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) { // the rest are wrong
            if (guess[jx].getD() == TilePair.UNCHECKED) {
                guess[jx].setD(TilePair.INCORRECT);
                WIN = false;
            }
        }
        return WIN;
    }

    public int getRowsDone() {
        return rowsDone;
    }
}