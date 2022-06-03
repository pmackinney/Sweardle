package com.captivepet.sweardle.ui.main;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintProperties;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;
import java.util.Locale;

// https://stackoverflow.com/questions/56021360/how-to-create-vertical-or-horizontal-guideline-programmatically

/**
 * Builds the keyboard which never changes
 */
public class GameTools implements Runnable {
    
    private Context context;
    private int HEIGHT;
    private int WIDTH;
    private int KEY_HEIGHT;
    private int KEY_WIDTH;
    private int KEY_MARGIN;
    private int KEYBOARD_MARGIN;
    private final int KEY_WIDTH_DIVISOR = 12; // How many keys wide is the full KB width?
    private final int KEY_HEIGHT_DIVISOR = 4; // How many keys tall is the min KB height?
    private final int KEY_MARGIN_DIVISOR = 12; // How many margins = 1 key?
    private final float MAX_KEY_HEIGHT_RATIO = 1.5f; // Max keyHeight relative to keyWidth
    
    public int getHeight() {
        return HEIGHT;
    }
    public int getWidth() {
        return WIDTH;
    }
    public int getKeyHeight() {
        return KEY_HEIGHT;
    }
    public int getKeyWidth() {
        return KEY_WIDTH;
    }

    public int getKeyMargin() {
        return KEY_MARGIN;
    }

    public int getKeyBoardHeight() {
        return 3 * KEY_HEIGHT + 2 * KEY_MARGIN + 2 * KEYBOARD_MARGIN;
    }
    public int getKeyBoardWidth() {
        return KEY_WIDTH * KEY_WIDTH_DIVISOR;
    }
    public int getKeyboardMargin() {
        return KEYBOARD_MARGIN;
    }


    void setSize(Point size) {
        this.WIDTH = size.x;
        this.HEIGHT = size.y;
    }
    public void computeSizes(int x, int y) {
        this.WIDTH = x;
        this.HEIGHT = y;
        if (y >= x) { // portrait
            KEY_WIDTH = x / KEY_WIDTH_DIVISOR;
            KEY_HEIGHT = Math.min((y - x) / KEY_HEIGHT_DIVISOR, (int) (MAX_KEY_HEIGHT_RATIO * KEY_WIDTH));
            KEY_MARGIN = 2;
        } else { // landscape
            KEY_WIDTH = (x - (int) Math.min(x - y, x / 2f)) / KEY_WIDTH_DIVISOR;
            KEY_HEIGHT = (int) (MAX_KEY_HEIGHT_RATIO * KEY_WIDTH);
            KEY_MARGIN = 1;
        }
        KEY_MARGIN = KEY_WIDTH / KEY_MARGIN_DIVISOR;
        KEYBOARD_MARGIN = (KEY_HEIGHT / 2) - (2 * KEY_MARGIN);
    }

    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    public static final char BLANK = ' ';
//    public static final char ENTER = '⏎';
//    public static final char DEL = '⌫';
    public static char ENTER;
    public static char DEL;
    private static final Button[] key = new Button[KEY_LABEL.length];
    private ImageButton enterButton;
    private ImageButton delButton;

    public GameTools(Context context) {
        this.context = context;
    }
    
    @Override
    public void run() {

    }

    public void computeSizes(Point size) {
        int keyboardHeight, keyboardWidth, gameboardSize;
        int PORTRAIT = 0;
        int LANDSCAPE = 1;
        int keyboardOrientation;
        if (size.y >= size.x) { // portrait
            keyboardWidth = size.x;
            KEY_WIDTH = keyboardWidth / KEY_WIDTH_DIVISOR;
            KEY_MARGIN = KEY_WIDTH / KEY_MARGIN_DIVISOR;
            keyboardHeight = Math.max(size.y - size.x, 4 * KEY_WIDTH);
            KEY_HEIGHT = Math.min(keyboardHeight / 4, (int) (MAX_KEY_HEIGHT_RATIO * KEY_WIDTH));
        } else { // landscape
            gameboardSize = (int) Math.min(size.x - size.y, size.x / 2f);
            keyboardWidth = size.x - (int) gameboardSize;
            KEY_WIDTH = keyboardWidth / KEY_WIDTH_DIVISOR;
            KEY_MARGIN = 1;
            KEY_HEIGHT = (int) (MAX_KEY_HEIGHT_RATIO * KEY_WIDTH);
        }
    }

//    public float getNewTotalWeight() {
//        return newTotalWeight;
//    }
//    public float getNewKeyboardWeight() {
//        return newKeyboardWeight;
//    }
//    public void makeKeyboard() {
//        // make the keys
//        ENTER = context.getString(R.string.enter_symbol).charAt(0);
//        enterButton = new ImageButton(context);
//        enterButton.setBackground(AppCompatResources.getDrawable(context, R.drawable.frame_highlight));
//        enterButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_keyboard_return_24));
//        enterButton.setTag(context.getString(R.string.enter_symbol));
//        setCommonProperties(enterButton);
//
//        DEL = context.getString(R.string.del_symbol).charAt(0);
//        delButton = new ImageButton(context);
//        delButton.setBackground(AppCompatResources.getDrawable(context, R.drawable.frame_highlight));
//        delButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_undo_24));
//        delButton.setTag(context.getString(R.string.del_symbol));
//        setCommonProperties(delButton);
//
//        for (int ix = 0; ix < key.length; ix++) {
//            Button k;
//            k = new Button(mLayout.getContext());
//            setCommonProperties(k);
//            k.setMaxLines(1);
//            k.setGravity(Gravity.CENTER);
//            k.setText(String.format(Locale.US, "%c", KEY_LABEL[ix]));
//            k.setTag(TilePair.UNCHECKED);
//            k.setBackground(AppCompatResources.getDrawable(context, TilePair.UNCHECKED));
//            key[ix] = (Button) k;
//        }
//
//        // label & position the keys
//        ConstraintSet set = new ConstraintSet();
//        set.clone(mLayout);
//        for (int ix = 0; ix < key.length; ix++) {
//            View k = key[ix];
//            int id = k.getId();
//            // set key widths & text
//            set.constrainWidth(id, keyWidth);
//            set.constrainHeight(id, keyHeight);
//            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;
//
//            // top & bottom constraints
//            if (ix == 0) {
//                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_SPREAD);
//                top = ConstraintSet.PARENT_ID;
//                topS = ConstraintSet.TOP;
//                topM = keyHeight / 2;
//                bottom = key[10].getId();
//                bottomS = ConstraintSet.TOP;
//                bottomM = keyMargin;
//            } else if (ix == 10) {
//                top = key[0].getId();
//                topS = ConstraintSet.BOTTOM;
//                topM = keyMargin;
//                bottom = key[19].getId();
//                bottomS = ConstraintSet.TOP;
//                bottomM = keyMargin;
//            } else if (ix == 19) {
//                top = key[10].getId();
//                topS = ConstraintSet.BOTTOM;
//                topM = keyMargin;
//                bottom = ConstraintSet.PARENT_ID;
//                bottomS = ConstraintSet.BOTTOM;
//                bottomM = keyHeight / 2;
//            } else {
//                top = key[ix - 1].getId();
//                topS = ConstraintSet.TOP;
//                topM = 0;
//                bottom = key[ix - 1].getId();
//                bottomS = ConstraintSet.BOTTOM;
//                bottomM = 0;
//            }
//            // left & right constraints
//            if (ix == 0 || ix == 10 || ix == 19){
//
//                if (ix == 19) {
//                    left = enterButton.getId();
//                    leftS = ConstraintSet.RIGHT;
//                    leftM = keyMargin;
//                } else {
//                    set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
//                    left = ConstraintSet.PARENT_ID;
//                    leftS = ConstraintSet.LEFT;
//                    leftM = keyMargin;
//                }
//                right = key[ix + 1].getId();
//                rightS = ConstraintSet.LEFT;
//                rightM = keyMargin;
//            } else if (ix == 9 || ix == 18 || ix == 25) {
//                left = key[ix - 1].getId();
//                leftS = ConstraintSet.RIGHT;
//                leftM = keyMargin;
//                if (ix == 25) {
//                    right = delButton.getId();
//                    rightS = ConstraintSet.LEFT;
//                    rightM = keyMargin;
//                } else {
//                    right = ConstraintSet.PARENT_ID;
//                    rightS = ConstraintSet.RIGHT;
//                    rightM = keyMargin;
//                }
//            } else {
//                left = key[ix - 1].getId();
//                leftS = ConstraintSet.RIGHT;
//                leftM = keyMargin;
//                right = key[ix + 1].getId();
//                rightS = ConstraintSet.LEFT;
//                rightM = keyMargin;
//            }
//            set.connect(id, ConstraintSet.TOP, top, topS, topM);
//            set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, bottomM);
//            set.connect(id, ConstraintSet.LEFT, left, leftS, leftM);
//            set.connect(id, ConstraintSet.RIGHT, right, rightS, rightM);
//        }
//        set.connect(enterButton.getId(), ConstraintSet.TOP, key[19].getId(), ConstraintSet.TOP, 0);
//        set.connect(enterButton.getId(), ConstraintSet.BOTTOM, key[19].getId(), ConstraintSet.BOTTOM, 0);
//        set.connect(enterButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, keyMargin);
//        set.connect(enterButton.getId(), ConstraintSet.RIGHT, key[19].getId(), ConstraintSet.LEFT, keyMargin);
//        set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);
//        set.connect(delButton.getId(), ConstraintSet.TOP, key[25].getId(), ConstraintSet.TOP, 0);
//        set.connect(delButton.getId(), ConstraintSet.BOTTOM, key[25].getId(), ConstraintSet.BOTTOM, 0);
//        set.connect(delButton.getId(), ConstraintSet.LEFT, key[25].getId(), ConstraintSet.RIGHT, keyMargin);
//        set.connect(delButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, keyMargin);
//        set.constrainHeight(enterButton.getId(), keyHeight);
//        set.constrainHeight(delButton.getId(), keyHeight);
//        set.constrainWidth(enterButton.getId(), (int) (1.5f * keyWidth));
//        set.constrainWidth(delButton.getId(), (int) (1.5f * keyWidth));
//        set.applyTo(mLayout);
//
//        // special delButton setup for testing
//        delButton.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                toggleKeyBoardWidth(view);
//                return false;
//            }
//        });
//        set.applyTo(mLayout);
//    }
//
//    public void setCommonProperties(View button) {
//        button.setId(View.generateViewId());
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mViewModel.onKeyboard(view);
//            }
//        });
//        mLayout.addView(button);
//    }
//
//    public static int keyLookup(char c) {
//        for (int ix = 0; ix < KEY_LABEL.length; ix++) {
//            if (KEY_LABEL[ix] == c) {
//                return ix;
//            }
//        }
//        return BLANK;
//    }
//
//    private int toggleState = 0;
//    public void toggleKeyBoardWidth(View view) {
//        ConstraintSet set = new ConstraintSet();
//        set.clone(mLayout);
//        if (toggleState % 3 == 0) {
//            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD);
//            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_SPREAD);
//            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_SPREAD);
//            toggleState++;
//        } else if (toggleState % 3 == 1){
//            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
//            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
//            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
//            toggleState++;
//        } else if (toggleState % 3 == 2) {
//            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_PACKED);
//            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_PACKED);
//            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);
//            toggleState++;
//        } else if (toggleState % 4 == 0) {
//            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD);
//            toggleState++;
//        }else if (toggleState % 4 == 1) {
//            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
//            toggleState++;
//        } else if (toggleState % 4 == 2) {
//            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_PACKED);
//        }
//        set.applyTo(mLayout);
//    }
//
//    private void updateKeyboard(@NonNull String signal) {
//        if (signal.equals(GameFragment.RESET)) {
//            for (View k : key) {
//                k.setBackground(AppCompatResources.getDrawable(context, TilePair.UNCHECKED));
//            }
//        } else if (signal.equals(GameFragment.ROW_UPDATED)) {
//            for (TilePair guess : mViewModel.getCurrentRow()) {
//                int guessStatus = guess.getD();
//                Button mKey = key[keyLookup(guess.getC())];
//                int keyStatus = (int) mKey.getTag();
//                if (keyStatus == TilePair.MISPLACED || keyStatus == TilePair.UNCHECKED) {
//                    if (guessStatus == TilePair.CORRECT) {
//                        setKeyStatus(mKey, TilePair.CORRECT);
//                    } else if (guessStatus == TilePair.MISPLACED && keyStatus != TilePair.MISPLACED) {
//                        setKeyStatus(mKey, TilePair.MISPLACED);
//                    } else if (guessStatus == TilePair.INCORRECT && keyStatus != TilePair.INCORRECT) {
//                        setKeyStatus(mKey, TilePair.INCORRECT);
//                    }
//                }
//            }
//        }
//    }
//
//    private void setKeyStatus(Button key, int status) {
//        key.setBackground(AppCompatResources.getDrawable(context, status));
//        key.setTag(status);
//    }
    public static int getNewGuideline(Context context, ConstraintLayout layout, int orientation, Float percentage) {
        int mId = View.generateViewId();
        Guideline guideline = new Guideline(context);
        guideline.setId(mId);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);
        lp.orientation = orientation;
        guideline.setLayoutParams(lp);
        ConstraintProperties props = new ConstraintProperties(guideline);
        props.constrainHeight(ConstraintProperties.WRAP_CONTENT);
        props.constrainWidth(ConstraintProperties.WRAP_CONTENT);
        guideline.setGuidelinePercent(percentage);
        layout.addView(guideline);
        return mId;
    }
}
