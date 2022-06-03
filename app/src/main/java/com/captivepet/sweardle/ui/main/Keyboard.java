package com.captivepet.sweardle.ui.main;

import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintProperties;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

/**
 * Builds the immutable keyboard.
 */
public class Keyboard implements Runnable {
    private static final String TAG = "SWEARDLE-K";
    private final Context context;
    private ConstraintSet set;
    private MainViewModel mViewModel;
    final Button[] key = new Button[KEY_LABEL.length];
    private final GameTools g;
    private Point size;

    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    public static final char BLANK = ' ';
    public static final char ENTER = '⏎';
    public static final char DEL = '⌫';
    private ImageButton enterButton;
    private ImageButton delButton;
    private static final int FUNCTION_KEY_BACKGROUND = R.drawable.frame_highlight;
    private static final int ENTER_KEY_IMAGE = R.drawable.ic_baseline_keyboard_return_24;
    private static final int DEL_KEY_IMAGE = R.drawable.ic_baseline_undo_24;
    public Keyboard(Context context) {
        this.context = context;
        this.g = new GameTools(context);
    }

    @Override
    public void run() {

    }

    void setmViewModel(MainViewModel model) {
        this.mViewModel = model;
    }
    
    private ConstraintSet getConstraintSet(ConstraintLayout layout) {
        if (this.set == null) {
            this.set = new ConstraintSet();
            set.clone(layout);
        }
        return this.set;
    }

    public void setSize(Point size) {
        this.size = size;
    }
    /**
     * Helper method for keyboard at bottom/right of gameboard.
     * @param layout - layout
     */
    public void generateKeyboardBottomRight(ConstraintLayout layout) {
        this.g.computeSizes(size.x, size.y);
        // TODO GET DIMENSIONS FOR PROPER PERCENTAGE
        int x = g.getWidth();
        int y = g.getHeight();
        float uPercentage;
        float lPercentage;
        int fuckme = g.getKeyBoardHeight();
        if (x <= y) {
            uPercentage = 1.0f - g.getKeyBoardHeight() / (float) y;
            lPercentage = 0.0f;
        } else {
            uPercentage = 0.0f;
            lPercentage = Math.min(x / 2, g.getKeyBoardWidth());
        }

        int upperGuideline = GameTools.getNewGuideline(context, layout, ConstraintLayout.LayoutParams.HORIZONTAL, uPercentage);
        int leftGuideline = GameTools.getNewGuideline(context, layout, ConstraintLayout.LayoutParams.VERTICAL, lPercentage);
        generateKeyboard(layout, upperGuideline, ConstraintSet.BOTTOM,
        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, leftGuideline, ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
    }
    /**
     * Adds the keyboard to the provided layout, providing ids for the bounding constraint objects.
     * For example, if the bounds are parent at bottom and right, and guidelines at top and left,
     * the keyboard can be made to appear at the bottom of a portrait layout or the right side of
     * a landscape layout simply by repositioning the guidelines.
     * @param layout - layout
     * @param topId - horizontal guideline id
     * @param topSide - BOTTOM
     * @param bottomId - parent
     * @param bottomSide - BOTTOM
     * @param startId - vertical guideline id
     * @param startSide - RIGHT
     * @param endId - parent
     * @param endSide - LEFT
     */
    public void generateKeyboard(ConstraintLayout layout, int topId, int topSide,
                                 int bottomId, int bottomSide, int startId, int startSide,
                                 int endId, int endSide) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // make the keys
        enterButton = (ImageButton) inflater.inflate(R.layout.view_key_special, null);
        setIdandOnClick(layout, enterButton);
        enterButton.setImageDrawable(AppCompatResources.getDrawable(context, ENTER_KEY_IMAGE));
        enterButton.setTag("" + ENTER);
        layout.addView(enterButton);

        delButton = (ImageButton) inflater.inflate(R.layout.view_key_special, null);
        setIdandOnClick(layout, delButton);
        delButton.setImageDrawable(AppCompatResources.getDrawable(context, DEL_KEY_IMAGE));
        delButton.setTag("" + DEL);
        layout.addView(delButton);

        for (int ix = 0; ix < key.length; ix++) {
            Button k;
            k = (Button) inflater.inflate(R.layout.view_key, null);
            setIdandOnClick(layout, k);
            String keyLabel = "" + KEY_LABEL[ix];
            k.setText(keyLabel);
            key[ix] = k;
            layout.addView(k);
        }

        // constraints
        this.set = getConstraintSet(layout);

        // enter
        set.constrainWidth(enterButton.getId(), (int) (1.5f * g.getKeyWidth()));  // TODO magic number
        set.constrainHeight(enterButton.getId(), g.getKeyHeight());
        set.connect(enterButton.getId(), ConstraintSet.TOP, key[19].getId(), ConstraintSet.TOP, 0);
        set.connect(enterButton.getId(), ConstraintSet.BOTTOM, key[19].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(enterButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, g.getKeyMargin());
        set.connect(enterButton.getId(), ConstraintSet.RIGHT, key[19].getId(), ConstraintSet.LEFT, g.getKeyMargin());
        set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);

        // del
        set.constrainWidth(delButton.getId(), (int) (1.5f * g.getKeyWidth()));
        set.constrainHeight(delButton.getId(), g.getKeyHeight());
        set.connect(delButton.getId(), ConstraintSet.TOP, key[25].getId(), ConstraintSet.TOP, 0);
        set.connect(delButton.getId(), ConstraintSet.BOTTOM, key[25].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(delButton.getId(), ConstraintSet.LEFT, key[25].getId(), ConstraintSet.RIGHT, g.getKeyMargin());
        set.connect(delButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, g.getKeyMargin());

        for (int ix = 0; ix < key.length; ix++) {
            View k = key[ix];
            int id = k.getId();
            // set key widths & text
            set.constrainWidth(id, g.getKeyWidth());
            set.constrainHeight(id, g.getKeyHeight());
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;

            // top & bottom constraints
            if (ix == 0) {
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_SPREAD);
                top = topId;
                topS = topSide;
                topM = g.getKeyboardMargin();
                bottom = key[10].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = g.getKeyMargin();
            } else if (ix == 10) {
                top = key[0].getId();
                topS = ConstraintSet.BOTTOM;
                topM = g.getKeyMargin();
                bottom = key[19].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = g.getKeyMargin();
            } else if (ix == 19) {
                top = key[10].getId();
                topS = ConstraintSet.BOTTOM;
                topM = g.getKeyMargin();
                bottom = bottomId;
                bottomS = bottomSide;
                bottomM = g.getKeyboardMargin();
            } else {
                top = key[ix - 1].getId();
                topS = ConstraintSet.TOP;
                topM = 0;
                bottom = key[ix - 1].getId();
                bottomS = ConstraintSet.BOTTOM;
                bottomM = 0;
            }
            // left & right constraints
            if (ix == 0 || ix == 10 || ix == 19){

                if (ix == 19) { // bottom row
                    left = enterButton.getId();
                    leftS = ConstraintSet.RIGHT;
                    leftM = g.getKeyMargin();
                } else { // top 2 rows
                    set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                    left = startId;
                    leftS = startSide;
                    leftM = g.getKeyMargin();
                }
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = g.getKeyMargin();
            } else if (ix == 9 || ix == 18 || ix == 25) {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = g.getKeyMargin();
                if (ix == 25) {
                    right = delButton.getId();
                    rightS = ConstraintSet.LEFT;
                    rightM = g.getKeyMargin();
                } else {
                    right = endId;
                    rightS = endSide;
                    rightM = g.getKeyMargin();
                }
            } else {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = g.getKeyMargin();
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = g.getKeyMargin();
            }
            set.connect(id, ConstraintSet.TOP, top, topS, topM);
            set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, bottomM);
            set.connect(id, ConstraintSet.LEFT, left, leftS, leftM);
            set.connect(id, ConstraintSet.RIGHT, right, rightS, rightM);
        }
        set.applyTo(layout);

        // special delButton setup for testing different arrangements
        delButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                toggleKeyBoardWidth(layout, view);
                return false;
            }
        });
    }

    public void setIdandOnClick(ConstraintLayout layout, View button) {
        try {
            button.setId(View.generateViewId());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewModel.onKeyboard(view);
                }
            });
        } catch (NullPointerException e) {
            Log.e(TAG, "MainViewModel not set? " + e.toString());
        }
    }

    public static int keyLookup(char c) {
        for (int ix = 0; ix < KEY_LABEL.length; ix++) {
            if (KEY_LABEL[ix] == c) {
                return ix;
            }
        }
        return BLANK;
    }

    private int toggleState = 0;
    public void toggleKeyBoardWidth(ConstraintLayout layout, View view) {
        if (toggleState % 3 == 0) {
            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD);
            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_SPREAD);
            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_SPREAD);
            toggleState++;
        } else if (toggleState % 3 == 1){
            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            toggleState++;
        } else if (toggleState % 3 == 2) {
            set.setHorizontalChainStyle(key[0].getId(), ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(key[10].getId(), ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);
            toggleState++;
        } else if (toggleState % 4 == 0) {
            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD);
            toggleState++;
        }else if (toggleState % 4 == 1) {
            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            toggleState++;
        } else if (toggleState % 4 == 2) {
            set.setVerticalChainStyle(key[0].getId(), ConstraintSet.CHAIN_PACKED);
        }
        set.applyTo(layout);
    }

    private void updateKeyboard(@NonNull String signal) {
        if (signal.equals(GameFragment.RESET)) {
            for (View k : key) {
                k.setBackground(AppCompatResources.getDrawable(context, TilePair.UNCHECKED));
            }
        } else if (signal.equals(GameFragment.ROW_UPDATED)) {
            for (TilePair guess : mViewModel.getCurrentRow()) {
                int guessStatus = guess.getD();
                Button mKey = key[keyLookup(guess.getC())];
                int keyStatus = (int) mKey.getTag();
                if (keyStatus == TilePair.MISPLACED || keyStatus == TilePair.UNCHECKED) {
                    if (guessStatus == TilePair.CORRECT) {
                        setKeyStatus(mKey, TilePair.CORRECT);
                    } else if (guessStatus == TilePair.MISPLACED && keyStatus != TilePair.MISPLACED) {
                        setKeyStatus(mKey, TilePair.MISPLACED);
                    } else if (guessStatus == TilePair.INCORRECT && keyStatus != TilePair.INCORRECT) {
                        setKeyStatus(mKey, TilePair.INCORRECT);
                    }
                }
            }
        }
    }

    private void setKeyStatus(Button key, int status) {
        key.setBackground(AppCompatResources.getDrawable(context, status));
        key.setTag(status);
    }
}
