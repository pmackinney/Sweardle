package com.captivepet.sweardle.ui.main;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

import com.captivepet.sweardle.TilePair;
import com.captivepet.sweardle.R;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> keyboardSignal;
    private MutableLiveData<String> gameSignal;

    final private ArrayList<TilePair> gameboard = new ArrayList<>();
    private final String[] dict;
    private final String[] words;
    private String gameWord;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.dict = application.getResources().getStringArray(R.array.dict);
        this.words = application.getResources().getStringArray(R.array.words);
    }

    public String getGameWord() {
        return gameWord;
    }

    public List<TilePair> getCurrentRow() {
        return gameboard.subList(getRowsDone() * GameFragment.WORD_LENGTH, gameboard.size());
    }

    public List<TilePair> getGameboard() {
        return gameboard;
    }

    public int getRowsDone() {
        return gameboard.size() / GameFragment.WORD_LENGTH;
    }

    public MutableLiveData<String> getKeyboardSignal() {
        if (keyboardSignal == null) {
            keyboardSignal = new MutableLiveData<>();
        }
        return keyboardSignal;
    }

    public MutableLiveData<String> getGameSignal() {
        if (gameSignal == null) {
            gameSignal = new MutableLiveData<>();
        }
        return gameSignal;
    }

    public void newGame() {
        gameboard.clear();
        gameWord = getNewGameWord();
    }

    private String getNewGameWord() {
        return words[(int) (Math.random() * words.length)];
    }

    public boolean validateGuess() {
        List<TilePair> currentRow = getCurrentRow();
        int rowsDone = gameboard.size() / GameFragment.WORD_LENGTH;
        int position = gameboard.size() - rowsDone * GameFragment.WORD_LENGTH;
        if (position == GameFragment.WORD_LENGTH) {
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
        List<TilePair> currentRow = getCurrentRow();
        char[] word = gameWord.toCharArray();
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find all correct letters
            guess[ix] = currentRow.get(currentRow.size() - GameFragment.WORD_LENGTH + ix);
            if (word[ix] == guess[ix].getChar()) {
                guess[ix].set(TilePair.CORRECT);
                word[ix] = GameFragment.EMPTY;
            }
        }
        boolean WIN = true;
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find misplaced
            if (word[ix] == GameFragment.EMPTY) {
                continue;
            }
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (word[ix] == guess[jx].getChar() && guess[jx].getStatusId() == TilePair.UNCHECKED) {
                    guess[jx].set(TilePair.MISPLACED);
                    WIN = false;
                    break; // word[ix] only gets one match
                }
            }
        }
        for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) { // the rest are wrong
            if (guess[jx].getStatusId() == TilePair.UNCHECKED) {
                guess[jx].set(TilePair.INCORRECT);
                WIN = false;
            }
        }
        return WIN;
    }

    public TilePair get(int ix) {
        return gameboard.get(ix);
    }

    public int getPosition() {
        return gameboard.size();
    }

    public void addChar(char k) {
        if (gameboard.size() < GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT) {
            gameboard.add(new TilePair(k, TilePair.UNCHECKED));
        }
    }
}