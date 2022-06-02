package com.captivepet.sweardle.ui.main;

import android.app.Application;
import android.service.quicksettings.Tile;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import com.captivepet.sweardle.TilePair;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<String> keyboardSignal;
    private MutableLiveData<String> gameSignal;

    // Game data
    /**
     * The dictionary of allowable guesses
     */
    private String[] dict;
    /**
     * The dictionary of possible solutions
     */
    private String[] words;
    /**
     * The solution
     */
    private String gameWord;
    /**
     * The array of previous guesses, immutable for the rest of the game.
     * Its size is always a multiple of GameFragment.WORD_LENGTH.
     */
    final private ArrayList<TilePair> completedRows = new ArrayList<>();
    /**
     * The current row. It becomes a guess when the player taps ENTER.
     */
    final private ArrayList<TilePair> currentRow = new ArrayList<>();

    // Logic controls
    private boolean ROW_TESTED = false;
    private boolean WINNER = false;


    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public String getGameWord() {
        return gameWord;
    }

    public ArrayList<TilePair> getCurrentRow() {
        return currentRow;
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
        newRow();
        WINNER = false;
        gameWord = selectGameWord();
    }

    private void newRow() {
        currentRow.clear();
        ROW_TESTED = false;
    }

    private String selectGameWord() {
        return words[(int) (Math.random() * words.length)];
    }

    private void setDictionary(String[] dict) {
        this.dict = dict;
    }
    private void setWords(String[] words) {
        this.words = words;
    }
    public void onKeyboard(View view) {
        char keyChar = getChar(view);
        int position = currentRow.size();
        if (keyChar == Keyboard.ENTER) {
            if (!ROW_TESTED && position == GameFragment.WORD_LENGTH) {
                if (validateGuess()) {
                    WINNER = testWord();
                    getGameSignal().setValue(GameFragment.ROW_UPDATED);
                    getKeyboardSignal().setValue(GameFragment.ROW_UPDATED);
                    newRow();
                    if (WINNER) {
                        getGameSignal().setValue(GameFragment.WINNER);
                    } if (!WINNER && (completedRows.size() / GameFragment.WORD_LENGTH == GameFragment.ROW_COUNT)) {
                        getGameSignal().setValue(GameFragment.LOSER);
                    }
                } else {
                    getGameSignal().setValue(GameFragment.BAD_WORD);
                }
            }
        } else if (keyChar == Keyboard.DEL) {
            if (!ROW_TESTED && position > 0) {
                currentRow.remove(position - 1);
                getGameSignal().setValue(GameFragment.DEL);
            }
        } else { // letter
            if (ROW_TESTED && position == GameFragment.WORD_LENGTH) {
                newRow();
            }
            if (position < GameFragment.WORD_LENGTH) {
                currentRow.add(new TilePair(keyChar, TilePair.UNCHECKED));
                getGameSignal().setValue(GameFragment.ADD_CHAR);
            }
        }
    }

    private char getChar(View view) {
        if (view.getTag().toString().charAt(0) == Keyboard.ENTER) {
            return Keyboard.ENTER;
        } else if (view.getTag().toString().charAt(0) == Keyboard.DEL) {
            return Keyboard.DEL;
        } else {
            return ((Button) view).getText().charAt(0);
        }
    }

    private boolean validateGuess() {
        int position = currentRow.size();
        if (position == GameFragment.WORD_LENGTH) {
            char[] guessWord = new char[GameFragment.WORD_LENGTH];
            for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) {
                guessWord[ix] = currentRow.get(ix).getC();
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

    private boolean testWord() {
        TilePair[] guess = new TilePair[GameFragment.WORD_LENGTH];
        char[] word = gameWord.toCharArray();
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find all correct letters
            guess[ix] = currentRow.get(currentRow.size() - GameFragment.WORD_LENGTH + ix);
            if (word[ix] == guess[ix].getC()) {
                guess[ix].setD(TilePair.CORRECT);
                word[ix] = GameFragment.EMPTY;
            }
        }
        boolean WIN = true;
        for (int ix = 0; ix < GameFragment.WORD_LENGTH; ix++) { // find misplaced
            if (word[ix] == GameFragment.EMPTY) {
                continue;
            }
            for (int jx = 0; jx < GameFragment.WORD_LENGTH; jx++) {
                if (word[ix] == guess[jx].getC() && guess[jx].getD() == TilePair.UNCHECKED) {
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
        ROW_TESTED = true;
        return WIN;
    }

    public int getRowsDone() {
        return completedRows.size() / GameFragment.WORD_LENGTH;
    }
}