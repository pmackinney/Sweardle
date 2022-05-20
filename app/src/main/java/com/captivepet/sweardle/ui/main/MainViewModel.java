package com.captivepet.sweardle.ui.main;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import com.captivepet.sweardle.R;

public class MainViewModel extends ViewModel {

    public static final char[] LETTERS = new char[]{'Q', 'W', 'E', 'R',  'T','Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            '+', 'Z', 'X', 'C','V', 'B','N', 'M', '-'};
    private final HashMap<Character, Integer> keyMap = new HashMap<Character, Integer>(28);
    final static int READY_STATE = 0;
    final static int WAITING_STATE = 1;
    final static int DONE_STATE = 3;
    int state = READY_STATE;
    final static int ENTER = 19;
    final static int DEL = 27;
    final static int ROW_COUNT = 6;
    final static int WORD_LENGTH = 5;

    String[] colors = new String[]{"white", "green", "grey", "ochre"};
    int charCount = 0;
    int lastColor = 0;
    char[] gameBoard = new char[ROW_COUNT * WORD_LENGTH];
    int gamePointer;

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    private String currentWord;
    MainFragment mFragment;

    void init() {
        for (int ix = 0; ix < LETTERS.length; ix++) {
            keyMap.put(LETTERS[ix], ix);
        }
    }

    public void setMainFragment(MainFragment mf) {
        this.mFragment = mf;
    }

    public void keyTap(View view) {
        String key = "";
        int ix;
        if (view instanceof Button) {
            key = ((Button) view).getText().toString();
        } else if (view instanceof CustomImageButton) {
            key = ((CustomImageButton) view).getText().toString();
        }
       ix = keyMap.get(key);
        switch(state) {
        case WAITING_STATE:
            if (ix == ENTER) {
                testWord();
            } else if (ix == DEL) {
                backspace();
            }
            break;
        case DONE_STATE:
            break;
        default:
            if (ix == DEL) {
                backspace();
            } else if (ix != ENTER) {
                addTile(key);
            }
        }
    }

    private void addTile(String key) {
        Drawable color = mFragment.getContext().getDrawable(R.drawable.frame_white);
        gameBoard[gamePointer] = key.charAt(0);
        mFragment.update(gamePointer, key, color);
        gamePointer++;
    }

    private void testWord() {
        boolean win = true;
        if (gamePointer % WORD_LENGTH == 0) {
            Drawable color = mFragment.getContext().getDrawable(R.drawable.frame_grey);
            for (int ix = 0; ix < WORD_LENGTH; ix++) {
                if (currentWord.charAt(ix) == gameBoard[gamePointer - WORD_LENGTH + ix]) {
                    color = mFragment.getContext().getDrawable(R.drawable.frame_green);
                } else if (currentWord.contains(String.valueOf(gameBoard[gamePointer - WORD_LENGTH + ix]))) {
                    color = mFragment.getContext().getDrawable(R.drawable.frame_ochre); // TODO check for letter count in word
                    win = false;
                } else {
                    win = false;
                }
            }
            mFragment.update(gamePointer, color);
            gamePointer++;
            if (win) {
                state = DONE_STATE;
            }
        }
    }

    private void backspace() {
        if (gamePointer % WORD_LENGTH > 0) {
            gamePointer--;
            mFragment.update(gamePointer, "", mFragment.getContext().getDrawable(R.drawable.frame_white));
        }
    }
}