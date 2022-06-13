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

/**
 * Supports a LiveData array representing and array of six five-letter words.
 * Each element is a tilePair, consisting of a char and a status (resource id).
 */
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
     * Returns the last row of untested letters
     * @return the last row
     */
    public List<TilePair> getLastRow() {
        int start = getRowsDone() * GameFragment.WORD_LENGTH;
        int end = getPosition();
        return getPairList().subList(start, end);
    }

    public LiveData<List<TilePair>> getGameboard() {
        if (gameboard.getValue() == null) {
            gameboard.setValue(new ArrayList<>(GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT));
        }
        return gameboard;
    }

    protected List<TilePair> getPairList() {
        LiveData<List<TilePair>> g = getGameboard();
        return g.getValue();
    }

    public char getTileChar(int ix) {
        return getPairList().get(ix).getChar();
    }
    private void setTileStatus(int ix, int status) {
        getPairList().get(ix).setStatus(status);
    }
    public int getTileStatus(int ix) {
        return getPairList().get(ix).getStatus();
    }

    /**
     * Returns the number of rows that have been completed and tested.
     * @return the number of rows done
     */
    public int getRowsDone() {
        int rowsDone = getPairList().size() / GameFragment.WORD_LENGTH;
        if (rowsDone > 0 && getTileStatus(rowsDone - 1) == TilePair.UNCHECKED) {
            rowsDone -= 1;
        }
        return rowsDone;
    }

    public void newGame() {
        getPairList().clear();
        solution = getNewGameWord();
//        solution = "STEED"; // TODO
    }

    private String getNewGameWord() {
        return words[(int) (Math.random() * words.length)];
    }

    public void deleteLastChar() {
        if (getPairList().size() > 0) {
            getPairList().remove(getPairList().size() - 1);
        }
        gameboard.setValue(gameboard.getValue()); // alert listeners
    }
    public TilePair getLastChar() {
        if (getPairList().size() > 0) {
            return getPairList().get(getPairList().size() - 1);
        } else {
            return null;
        }
    }

    public boolean validateGuess() {
        int rowsDone = getPairList().size() / GameFragment.WORD_LENGTH;
        int rowStart = (rowsDone - 1) * GameFragment.WORD_LENGTH;
        int guessLength = getPairList().size() - rowStart;
        if (guessLength == GameFragment.WORD_LENGTH) {
            char[] guessWord = new char[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                guessWord[ix] = getPairList().get(rowStart + ix).getChar();
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
        int offset = getPosition() - GameFragment.WORD_LENGTH;
        if (getCurrentRow() * GameFragment.WORD_LENGTH != offset) { // only test complete rows
            return false;
        }
        char[] word = solution.toCharArray();
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find all correct letters
            if (word[ix] == getTileChar(offset + ix)) {
                setTileStatus(ix, TilePair.CORRECT);
            }
        }
        boolean WIN = true;
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find misplaced
            for (int jx = offset; jx < offset + GameFragment.WORD_LENGTH; jx++) {
                if (getTileStatus(jx) != TilePair.CORRECT && word[ix] == getTileChar(jx)) {
                        setTileStatus(jx, TilePair.MISPLACED);
                        WIN = false;
                        break;
                }
            }
        }
        for (int jx = offset; jx < offset + GameFragment.WORD_LENGTH; jx++) { // the rest are incorrect
            if (getTileStatus(jx) == TilePair.UNCHECKED) {
                setTileStatus(jx, TilePair.INCORRECT);
                WIN = false;
            }
        }
        gameboard.setValue(gameboard.getValue());
        return WIN;
    }

    public TilePair get(int ix) {
        return getPairList().get(ix);
    }

    int getPosition() {
        return getPairList().size();
    }
    public int getCurrentRow() {
        int quotient = getPosition() / GameFragment.WORD_LENGTH;
        int remainder = getPosition() - quotient * GameFragment.WORD_LENGTH;
        if (quotient == 0 || remainder != 0) {
            return quotient;
        } else {
            return quotient - 1;
        }
    }

    public void addChar(char k) {
        if (getPairList().size() < GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT) {
            getPairList().add(new TilePair(k, TilePair.UNCHECKED));
            gameboard.setValue(gameboard.getValue());
        }
    }
}