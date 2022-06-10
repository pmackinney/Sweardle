package com.captivepet.sweardle.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import com.captivepet.sweardle.TilePair;
import com.captivepet.sweardle.R;

public class MainViewModel extends AndroidViewModel {
    private final String TAG = getClass().getSimpleName();

    // new
    private final MutableLiveData<List<TilePair>> gameboard = new MutableLiveData<>();

    private final String[] dict;
    private final String[] words;
    private String solution;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.dict = application.getResources().getStringArray(R.array.dict);
        this.words = application.getResources().getStringArray(R.array.words);
    }

    public String getSolution() {
        return solution;
    }

    /**
     * Returns the current guess, e.g., the last complete row of the gameboard.
     * @return
     */
    public List getGuessWord() {
        ArrayList<Character> guessWord = new ArrayList<Character>(5);
        int position = getValue().size();
        int rowsDone = getRowsDone();
        if (rowsDone > 0 && position % GameFragment.WORD_LENGTH == 0) {
            for (int ix = position - GameFragment.WORD_LENGTH; ix < position; ix++) {
                guessWord.add(getTileChar(ix));
            }
            return guessWord;
        } else {
            return null;
        }
    }

    private List<TilePair> getValue() {
        if (gameboard.getValue() == null) {
            gameboard.setValue(new ArrayList<>(GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT));
        }
        return gameboard.getValue();
    }

    public char getTileChar(int ix) {
        return getValue().get(ix).getChar();
    }

    public int getTileStatus(int ix) {
        return getValue().get(ix).getStatus();
    }
    public int getRowsDone() {
        return getValue().size() / GameFragment.WORD_LENGTH;
    }


    public void newGame() {
        gameboard.getValue().clear();
        solution = getNewGameWord();
//        solution = "STEED"; // TODO
    }

    private String getNewGameWord() {
        return words[(int) (Math.random() * words.length)];
    }

    public void deleteLastChar() {
        if (getValue().size() > 0) {
            getValue().remove(getValue().size() - 1);
        }
    }
    public char getLastChar() {
        return getTileChar(getValue().size() - 1);
    }

    public boolean validateGuess() {
        List<TilePair> currentRow = getGuessWord();
        int rowsDone = getValue().size() / GameFragment.WORD_LENGTH;
        int guessLength = getValue().size() - (rowsDone - 1) * GameFragment.WORD_LENGTH;
        if (guessLength == GameFragment.WORD_LENGTH) {
            char[] guessWord = new char[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                guessWord[ix] = currentRow.get(ix).getChar();
            }
            boolean VALID = false;
            for (String s : dict) {
                if (s.equals(String.valueOf(guessWord))) {
                    VALID = true;
                    break;
                }
            }
            return VALID;
        }
        return false;
    }

    public boolean testWord() {
        TilePair[] guess = new TilePair[GameFragment.WORD_LENGTH];
        List<TilePair> currentRow = getGuessWord();
        char[] word = solution.toCharArray();

        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find all correct letters
            guess[ix] = currentRow.get(currentRow.size() - GameFragment.WORD_LENGTH + ix);
            if (word[ix] == guess[ix].getChar()) {
                guess[ix].setStatus(TilePair.CORRECT);
                word[ix] = GameFragment.EMPTY;
            }
        }
        boolean WIN = true;
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find misplaced
            if (word[ix] == GameFragment.EMPTY) {
                continue;
            }
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (word[ix] == guess[jx].getChar() && guess[jx].getStatus() == TilePair.UNCHECKED) {
                    guess[jx].setStatus(TilePair.MISPLACED);
                    WIN = false;
                    break; // word[ix] only gets one match
                }
            }
        }

        for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) { // the rest are wrong
        if (guess[jx].getStatus() == TilePair.UNCHECKED) {
            guess[jx].setStatus(TilePair.INCORRECT);
            WIN = false;
        }
    }
        return WIN;
}

    public int getPosition() {
        return getValue().size();
    }

    public void addChar(char k) {
        if (getValue().size() < GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT) {
            getValue().add(new TilePair(k, TilePair.UNCHECKED));
        }
    }
}