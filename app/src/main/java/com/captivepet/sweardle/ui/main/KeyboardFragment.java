package com.captivepet.sweardle.ui.main;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.captivepet.sweardle.MainActivity;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

import java.util.List;
import java.util.Locale;

/**
 * Builds the keyboard which never changes
 */
public class KeyboardFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private MainViewModel mViewModel;
    private ConstraintLayout mLayout;
    private float keyboardWeight;
    private int keyHeight;
    private int keyWidth;
    private int keyMargin;
    float newTotalWeight;
    float newKeyboardWeight;
    private final int KEY_WIDTH_DIVISOR = 12; // How many keys wide is the full KB width?
    private final int KEY_MARGIN_DIVISOR = 12; // How many margins = 1 key?
    private final float MAX_KEY_HEIGHT_RATIO = 1.5f; // Max keyHeight relative to keyWidth

    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    public static final char firstLetter = 'A';
    public static final char lastLetter = 'Z';
    public static final char BLANK = ' ';
    public static final char ENTER = '⏎';
    public static final char DEL = '⌫';
    private static final Button[] key = new Button[KEY_LABEL.length];
    private ImageButton enterButton;
    private ImageButton delButton;

    // LiveData signals
    public static final String UPDATE_FROM_LAST_ROW = "Update";
    public static final String UPDATE_ALL = "Reset";

    public KeyboardFragment() {} // Required empty public constructor ??
    public static KeyboardFragment newInstance() { return new KeyboardFragment(); }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = view.findViewById(R.id.fragment_keyboard);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGameboard().observe(getViewLifecycleOwner(), this::onChanged);
    }

    public int computeSizes(Point size) {
        int keyboardHeight, keyboardWidth, gameboardSize;
        if (size.y >= size.x) { // portrait
            keyboardWidth = size.x;
            keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
            keyMargin = keyWidth / KEY_MARGIN_DIVISOR;
            keyboardHeight = Math.max(size.y - size.x, 4 * keyWidth);
            keyHeight = Math.min(keyboardHeight / 4, (int) (MAX_KEY_HEIGHT_RATIO * keyWidth));
            gameboardSize = size.y - keyboardHeight;
        } else { // landscape
            gameboardSize = (int) Math.min(size.x - size.y, size.x / 2f);
            keyboardWidth = size.x - gameboardSize;
            keyboardWeight = 1.0f - (gameboardSize / (float) size.x);
            keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
            keyMargin = 1;
            keyHeight = (int) (MAX_KEY_HEIGHT_RATIO * keyWidth);
        }
        return gameboardSize;
    }

    public float getNewTotalWeight() {
        return newTotalWeight;
    }
    public float getNewKeyboardWeight() {
        return newKeyboardWeight;
    }
    public void init() {
        // make the keys
        enterButton = new ImageButton(getContext());
        enterButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.frame_highlight));
        enterButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_return_24));
        enterButton.setTag(KeyboardFragment.ENTER);
        enterButton.setId(View.generateViewId());
        enterButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               ((MainActivity) getActivity()).onSpecialKey(enterButton);
                                           }
                                       });
        mLayout.addView(enterButton);

        delButton = new ImageButton(getContext());
        delButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.frame_highlight));
        delButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_undo_24));
        delButton.setTag(KeyboardFragment.DEL);
        delButton.setId(View.generateViewId());
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).onSpecialKey(delButton);
            }
        });
        mLayout.addView(delButton);

        for (int ix = 0; ix < key.length; ix++) {
            Button k;
            k = new Button(mLayout.getContext());
            k.setId(View.generateViewId());
            k.setText(String.format(Locale.US, "%c", KEY_LABEL[ix]));
            final String letter = String.valueOf(KEY_LABEL[ix]);
            k.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).onCharKey(k);
                }
            });
            mLayout.addView(k);
            k.setMaxLines(1);
            k.setGravity(Gravity.CENTER);

            k.setTag(TilePair.UNCHECKED);
            k.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
            key[ix] = k;
        }

        // label & position the keys
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < key.length; ix++) {
            View k = key[ix];
            int id = k.getId();
            // set key widths & text
            set.constrainWidth(id, keyWidth);
            set.constrainHeight(id, keyHeight);
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;

            // top & bottom constraints
            if (ix == 0) {
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
                topM = keyHeight / 2;
                bottom = key[10].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = keyMargin;
            } else if (ix == 10) {
                top = key[0].getId();
                topS = ConstraintSet.BOTTOM;
                topM = keyMargin;
                bottom = key[19].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = keyMargin;
            } else if (ix == 19) {
                top = key[10].getId();
                topS = ConstraintSet.BOTTOM;
                topM = keyMargin;
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
                bottomM = keyHeight / 2;
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

                if (ix == 19) {
                    left = enterButton.getId();
                    leftS = ConstraintSet.RIGHT;
                    leftM = keyMargin;
                } else {
                    set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                    left = ConstraintSet.PARENT_ID;
                    leftS = ConstraintSet.LEFT;
                    leftM = keyMargin;
                }
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            } else if (ix == 9 || ix == 18 || ix == 25) {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = keyMargin;
                if (ix == 25) {
                    right = delButton.getId();
                    rightS = ConstraintSet.LEFT;
                    rightM = keyMargin;
                } else {
                    right = ConstraintSet.PARENT_ID;
                    rightS = ConstraintSet.RIGHT;
                    rightM = keyMargin;
                }
            } else {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = keyMargin;
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            }
            set.connect(id, ConstraintSet.TOP, top, topS, topM);
            set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, bottomM);
            set.connect(id, ConstraintSet.LEFT, left, leftS, leftM);
            set.connect(id, ConstraintSet.RIGHT, right, rightS, rightM);
        }
        set.connect(enterButton.getId(), ConstraintSet.TOP, key[19].getId(), ConstraintSet.TOP, 0);
        set.connect(enterButton.getId(), ConstraintSet.BOTTOM, key[19].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(enterButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, keyMargin);
        set.connect(enterButton.getId(), ConstraintSet.RIGHT, key[19].getId(), ConstraintSet.LEFT, keyMargin);
        set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);
        set.connect(delButton.getId(), ConstraintSet.TOP, key[25].getId(), ConstraintSet.TOP, 0);
        set.connect(delButton.getId(), ConstraintSet.BOTTOM, key[25].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(delButton.getId(), ConstraintSet.LEFT, key[25].getId(), ConstraintSet.RIGHT, keyMargin);
        set.connect(delButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, keyMargin);
        set.constrainHeight(enterButton.getId(), keyHeight);
        set.constrainHeight(delButton.getId(), keyHeight);
        set.constrainWidth(enterButton.getId(), (int) (1.5f * keyWidth));
        set.constrainWidth(delButton.getId(), (int) (1.5f * keyWidth));
        set.applyTo(mLayout);

        // special delButton setup for testing
        delButton.setOnLongClickListener(view -> {
            toggleKeyBoardWidth(view);
            return false;
        });
        set.applyTo(mLayout);
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
    public void toggleKeyBoardWidth(View view) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
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
        set.applyTo(mLayout);
    }

    /**
     * Update the key highlights. Only need to check the last row as
     * completed rows are implicitly immutable.
     * @param gameboard - the array of TilePairs
     */
    private void onChanged(List<TilePair> gameboard) {
        int start = mViewModel.getRowsDone();
        int end = mViewModel.getPosition();
        Log.d(TAG, "start, end = " + start + ", " + end);
        Log.d(TAG, "subList.size() = " + gameboard.subList(start, end).size());
        for (TilePair guess : gameboard.subList(start, end)) {
            Button mKey = key[keyLookup(guess.getChar())];
            int guessStatus = guess.getStatus();
            int keyStatus = (int) mKey.getTag();

            if (keyStatus == TilePair.MISPLACED || keyStatus == TilePair.UNCHECKED) {
                if (guessStatus == TilePair.CORRECT) {
                    setKeyStatus(mKey, TilePair.CORRECT);
                    Log.d(TAG, TilePair.getStatusName(keyStatus) + " -> " + TilePair.getStatusName(TilePair.CORRECT));
                } else if (guessStatus == TilePair.MISPLACED && keyStatus != TilePair.MISPLACED) {
                    setKeyStatus(mKey, TilePair.MISPLACED);
                    Log.d(TAG, TilePair.getStatusName(keyStatus) + " -> " + TilePair.getStatusName(TilePair.MISPLACED));
                } else if (guessStatus == TilePair.INCORRECT) {
                    Log.d(TAG, TilePair.getStatusName(keyStatus) + " -> " + TilePair.getStatusName(TilePair.INCORRECT));
                    setKeyStatus(mKey, TilePair.INCORRECT);
                }
            }
        }
    }

    private void setKeyStatus(Button key, int status) {
        key.setBackground(AppCompatResources.getDrawable(requireContext(), status));
        key.setTag(status);
    }
}
