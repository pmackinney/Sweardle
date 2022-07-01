package com.captivepet.sweardle.ui.main;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

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

    // https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate


    // new
    private final MutableLiveData<List<TilePair>> gameboard = new MutableLiveData<>();

    private final String[] dict;
    private final String[] words;
    private String solution;
    private SavedStateHandle state;
    private final String GUESS_TESTED_KEY = "guess";
    private final String SOLUTION_KEY = "solution";

    public MainViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.dict = application.getResources().getStringArray(R.array.dict);
        this.words = application.getResources().getStringArray(R.array.words);
        this.state = savedStateHandle;
    }

    public String getSolution(boolean NEW) {
        if (solution == null || NEW) {
            this.solution = getNewGameWord();
        }
        return solution;
    }

    /**
     * Returns the last row of untested letters
     *
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
        List<TilePair> pairList = getPairList();
        if (ix < pairList.size()) {
            return getPairList().get(ix).getChar();
        } else {
            return KeyboardFragment.BLANK;
        }
    }

    private void setTileStatus(int ix, int status) {
        getPairList().get(ix).setStatus(status);
    }

    public int getTileStatus(int ix) {
        List<TilePair> pairList = getPairList();
        if (ix < pairList.size()) {
            return getPairList().get(ix).getStatus();
        } else {
            return TilePair.UNCHECKED;
        }
    }

    /**
     * Returns the number of rows that have been completed and tested.
     *
     * @return the number of rows done
     */
    public int getRowsDone() {
        int rowsDone = getPairList().size() / GameFragment.WORD_LENGTH;
        if (rowsDone > 0 && getTileStatus(rowsDone * GameFragment.WORD_LENGTH - 1) == TilePair.UNCHECKED) {
            rowsDone -= 1;
        }
        return rowsDone;
    }

    public void newGame() {
        getPairList().clear();
        gameboard.setValue(getPairList());
        solution = getSolution(true);
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

    /**
     * Returns a row as a char[]
     *
     * @param row the row to return
     * @return the guess
     */
    private char[] getGuess(int row) {
        if (0 <= row && row <= getRowsDone()) {
            char[] guess = new char[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                guess[ix] = getTileChar(row * GameFragment.WORD_LENGTH + ix);
            }
            return guess;
        }
        return null;
    }

    public boolean testWord() {
        boolean RESULT = true;
        char skip = '+'; // prevent matching capital letters
        int row = getRowsDone();
        char[] guess = getGuess(row);
        if (guess == null) {
            return false;
        }
        char[] solution = getSolution(false).toCharArray();
        int offset = getRowsDone() * GameFragment.WORD_LENGTH;

        // check for correct letters
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
            if (solution[ix] != skip && solution[ix] == guess[ix]) {
                setTileStatus(offset + ix, TilePair.CORRECT);
                solution[ix] = skip;
            }
        }
        // check for misplaced letters
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // check for misplaced letters
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (solution[ix] != skip && solution[ix] == guess[jx] && getTileStatus(jx) == TilePair.UNCHECKED) {
                    RESULT = false;
                    setTileStatus(offset + jx, TilePair.MISPLACED);
                    solution[ix] = skip;
                }
            }
        }
        // remainder are incorrect
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // the rest are incorrect
            if (getTileStatus(offset + ix) == TilePair.UNCHECKED) {
                RESULT = false;
                setTileStatus(offset + ix, TilePair.INCORRECT);
            }
        }
        // notify listeners
        gameboard.setValue(gameboard.getValue());
        return RESULT;
    }

    int getPosition() {
        List<TilePair> mList = getPairList();
        int s = mList.size();
        return getPairList().size();
    }

    public void addChar(char k) {
        if (getPairList().size() < GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT) {
            getPairList().add(new TilePair(k, TilePair.UNCHECKED));
            gameboard.setValue(gameboard.getValue());
        }
    }
}
