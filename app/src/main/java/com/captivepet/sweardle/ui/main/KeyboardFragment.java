package com.captivepet.sweardle.ui.main;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Locale;

import com.captivepet.sweardle.MainActivity;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;


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
    private int specialKeyWidth;
    private int keyMargin;
    float newTotalWeight;
    float newKeyboardWeight;
    final int KEY_WIDTH_DIVISOR = 12; // How many keys wide is the full KB width?
    final int KEY_MARGIN_DIVISOR = 12; // How many margins = 1 key?
    public static final float KEYBOARD_WIDTH_TO_HEIGHT_RATIO = 2 / 3F;
    public static final float SPECIAL_KEYWIDTH_RATIO = 1.5F;
    final float MAX_KEY_HEIGHT_RATIO = 1.5f; // Max keyHeight relative to keyWidth

    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    public static final char firstLetter = 'A';
    public static final char lastLetter = 'Z';
    public static final char BLANK = ' ';
    public static final char ENTER = '⏎';
    public static final char DEL = '⌫';
    private static final AppCompatButton[] key = new AppCompatButton[KEY_LABEL.length];
    private ImageButton enterButton;
    private ImageButton delButton;

    public KeyboardFragment() {
    } // Required empty public constructor ??

    public static KeyboardFragment newInstance() {
        return new KeyboardFragment();
    }

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

    private void computeSizes(int keyboardWidth) {
        keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
        specialKeyWidth = (int) (SPECIAL_KEYWIDTH_RATIO * keyWidth);
        keyMargin = Math.max(1, keyWidth / KEY_MARGIN_DIVISOR);
        keyHeight = (int) (MAX_KEY_HEIGHT_RATIO * keyWidth);
    }

    public void init(int keyboardWidth) {
        computeSizes(keyboardWidth);
        // make the keys, label & set constraints
        if (enterButton == null) { //
            enterButton = new ImageButton(getContext());
            enterButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.frame_highlight));
            enterButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_return_24));
            enterButton.setTag(KeyboardFragment.ENTER);
            enterButton.setId(View.generateViewId());
            enterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity myActivity = (MainActivity) getActivity();
                    if (myActivity != null) {
                        myActivity.onSpecialKey(enterButton);
                    }
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
                    MainActivity myActivity = (MainActivity) getActivity();
                    if (myActivity != null) {
                        myActivity.onSpecialKey(delButton);
                    }
                }
            });
            mLayout.addView(delButton);

            for (int ix = 0; ix < key.length; ix++) {
                AppCompatButton k;
                k = new AppCompatButton(mLayout.getContext());
                k.setId(View.generateViewId());
                k.setText(String.format(Locale.US, "%c", KEY_LABEL[ix]));
                k.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity myActivity = (MainActivity) getActivity();
                        if (myActivity != null) {
                            myActivity.onCharKey(k);
                        }
                    }
                });
                mLayout.addView(k);
                k.setMaxLines(1);
                k.setGravity(Gravity.CENTER);
                k.setTag(TilePair.UNCHECKED);
                k.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
                key[ix] = k;
            }
        }

        // size & constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < key.length; ix++) {
            View k = key[ix];
            int id = k.getId();
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
            if (ix == 0 || ix == 10 || ix == 19) {

                if (ix == 19) {
                    left = enterButton.getId();
                    leftS = ConstraintSet.RIGHT;
                } else {
                    set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                    left = ConstraintSet.PARENT_ID;
                    leftS = ConstraintSet.LEFT;
                }
                leftM = keyMargin;
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
                } else {
                    right = ConstraintSet.PARENT_ID;
                    rightS = ConstraintSet.RIGHT;
                }
                rightM = keyMargin;
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
        set.constrainWidth(enterButton.getId(), specialKeyWidth);
        set.constrainWidth(delButton.getId(), specialKeyWidth);
        set.applyTo(mLayout);
        onChanged(mViewModel.getPairList(), true);
        Log.d(TAG, "init ends");
    }

    // label &
    public static int keyLookup(char c) {
        for (int ix = 0; ix < KEY_LABEL.length; ix++) {
            if (KEY_LABEL[ix] == c) {
                return ix;
            }
        }
        return BLANK;
    }

    /**
     * Update the key highlights. Only need to check the last row as
     * completed rows are implicitly immutable.
     *
     * @param pairList - the array of TilePairs
     */
    private void onChanged(List<TilePair> pairList) {
        onChanged(pairList, false);
    }
    /**
     * Update the key highlights, either all or just the last row.
     *
     * @param pairList - the array of TilePairs
     * @param decorateAll - boolean flag
     */
    private void onChanged(List<TilePair> pairList, boolean decorateAll) {
        int end = mViewModel.getPosition();
        if (decorateAll) {
            end = mViewModel.getPairList().size();
        }
        if (end > 0 && end % GameFragment.WORD_LENGTH == 0) { // only update complete rows
            for (TilePair tp : pairList.subList(end - GameFragment.WORD_LENGTH, end)) {
                AppCompatButton k = key[keyLookup(tp.getChar())];
                int kStatus = (int) k.getTag();
                if (kStatus == TilePair.MISPLACED && tp.getStatus() == TilePair.CORRECT) {
                    setKeyStatus(k, tp.getStatus());
                } else if (kStatus == TilePair.UNCHECKED) {
                    setKeyStatus(k, tp.getStatus());
                }
            }
        } else if (end == 0 && enterButton != null) {
            for (AppCompatButton k : key) {
                setKeyStatus(k, TilePair.UNCHECKED);
            }
        }
    }

    private void setKeyStatus(AppCompatButton key, int status) {
        key.setBackground(AppCompatResources.getDrawable(requireContext(), status));
        key.setTag(status);
    }
}
