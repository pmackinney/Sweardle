package com.captivepet.sweardle.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

/**
 * Builds the keyboard which never changes
 */
public class KeyboardFragment extends Fragment {

    private MainViewModel mViewModel;
    private ConstraintLayout mLayout;
    private int keyHeight;
    private int keyWidth;
    private int keyMargin;
    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    private static final Button[] key = new Button[KEY_LABEL.length];
    public static final char BLANK = ' ';

    private final static int KEY_WIDTH_DIVISOR = 12;
    private final static int KEY_MARGIN_DIVISOR = 9;
    private final static float KEY_HEIGHT_TO_WIDTH_RATIO = 1.5f;
    private ImageButton toggle;
    private Button qKey = key[0];
    private Button aKey = key[10];
    private ImageButton enterKey;

    public KeyboardFragment() {
        // Required empty public constructor ??
    }

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
        // The livedata object update tells us when to update the key highlighting
        // to reflect which letters are correct, misplaced, or incorrect.
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getKeyboardSignal().observe(getViewLifecycleOwner(), this::updateKeyboard);
        mLayout = (ConstraintLayout) view;
    }

    /**
     * init() determines the keyboard size & key size,
     * then creates & positions the keys.
     */
    public int init() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mWidth = dm.widthPixels;
        int mHeight = dm.heightPixels;
        int gameboardSize, keyboardWidth, keyboardHeight;
        if (mHeight >= mWidth) { // portrait
            keyboardWidth = mWidth;
            keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
            keyMargin = keyWidth / KEY_MARGIN_DIVISOR;
            keyboardHeight = Math.max(mHeight - mWidth, 4 * keyWidth);
            gameboardSize = mHeight - keyboardHeight;
            keyHeight = Math.min(keyboardHeight / 4, (int) (KEY_HEIGHT_TO_WIDTH_RATIO * keyWidth));
        } else { // landscape
            gameboardSize = Math.min(mWidth - mHeight, mWidth / 2);
            keyboardWidth = mWidth - gameboardSize;
            keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
            keyMargin = 1;
            keyHeight = (int)(KEY_HEIGHT_TO_WIDTH_RATIO * keyWidth);
        }

        // make the keys
        for (int ix = 0; ix < KEY_LABEL.length; ix++) {
            key[ix] = new Button(getContext());
            key[ix].setMaxLines(1);
            key[ix].setGravity(Gravity.CENTER);
            key[ix].setText(String.valueOf(KEY_LABEL));
            setupKey(key[ix]);
            if (ix == 0) {
                qKey = key[ix];
            } else if (ix == 19) {
                aKey = key[ix];
            }
        }
        enterKey = new ImageButton(mLayout.getContext());
        setupKey(enterKey);
        enterKey.setImageResource(R.drawable.ic_baseline_keyboard_return_24);
        ImageButton del = new ImageButton(mLayout.getContext());
        setupKey(del);
        del.setImageResource(R.drawable.ic_baseline_undo_24);
        setToggleKey(del);

        // position the keys
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
                bottom = enterKey.getId();
                bottomS = ConstraintSet.TOP;
                bottomM = keyMargin;
            } else {
                top =  key[ix - 1].getId();
                topS = ConstraintSet.TOP;
                topM = keyMargin;
                bottom = key[ix - 1].getId();
                bottomS = ConstraintSet.BOTTOM;
                bottomM = keyMargin;
            }
            // left & right constraints
            if (ix == 0 || ix == 10) {
                set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                left = ConstraintSet.PARENT_ID;
                leftS = ConstraintSet.LEFT;
                leftM = keyMargin;
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            } else if (ix == 19) {
                left = enterKey.getId();
                leftS = ConstraintSet.RIGHT;
                leftM = keyMargin;
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            } else if (ix == 9 || ix == 18) {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = keyMargin;
                right = ConstraintSet.PARENT_ID;
                rightS = ConstraintSet.RIGHT;
                rightM = keyMargin;
            } else if (ix == 25) {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = keyMargin;
                right = del.getId();
                rightS = ConstraintSet.LEFT;
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
        set.constrainWidth(enterKey.getId(), (int) (1.5f * keyWidth));
        set.connect(enterKey.getId(), ConstraintSet.TOP, key[10].getId(), ConstraintSet.BOTTOM, keyMargin);
        set.connect(enterKey.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, keyMargin);
        set.connect(enterKey.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, keyMargin);
        set.connect(enterKey.getId(), ConstraintSet.RIGHT, key[19].getId(), ConstraintSet.LEFT, keyMargin);
        set.constrainWidth(del.getId(), (int) (1.5f * keyWidth));
        set.connect(del.getId(), ConstraintSet.TOP, key[18].getId(), ConstraintSet.BOTTOM, keyMargin);
        set.connect(del.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, keyMargin);
        set.connect(del.getId(), ConstraintSet.LEFT, key[19].getId(), ConstraintSet.LEFT, keyMargin);
        set.connect(del.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, keyMargin);
        set.applyTo(mLayout);
        return (gameboardSize);
    }

    private void makeKeys() {
    }

    public void setupKey(View key) {
        key.setTag(TilePair.UNCHECKED);
        key.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
        key.setId(View.generateViewId());
        mLayout.addView(key);
        key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.onKeyClick(view);
            }
        });
    }
    /**
     * Program a key to implement various Keyboard setups.
     * @param toggle
     */
    private void setToggleKey(ImageButton toggle) {
        this.toggle = toggle;
        toggle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                toggleKeyBoardWidth(view);
                return false;
            }
        });
    }
    public void toggleKeyBoardWidth(View view) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        if (toggle.getTag().toString().equals("spread")) {
            set.setHorizontalChainStyle(qKey.getId(), ConstraintSet.CHAIN_SPREAD);
            set.setHorizontalChainStyle(aKey.getId(), ConstraintSet.CHAIN_SPREAD);
            set.setHorizontalChainStyle(enterKey.getId(), ConstraintSet.CHAIN_SPREAD);
            toggle.setTag("packed");
        } else if (toggle.getTag().toString().equals("packed")){
            set.setHorizontalChainStyle(qKey.getId(), ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(aKey.getId(), ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(enterKey.getId(), ConstraintSet.CHAIN_PACKED);
            toggle.setTag("spread inside");
        } else {
            set.setHorizontalChainStyle(qKey.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            set.setHorizontalChainStyle(aKey.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            set.setHorizontalChainStyle(enterKey.getId(), ConstraintSet.CHAIN_SPREAD_INSIDE);
            toggle.setTag("spread");
        }
        set.applyTo(mLayout);
    }

    private void updateKeyboard(String signal) {
        if (signal.equals((GameFragment.RESET_KEYS))) { // new game
            for (View k : key) {
                k.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
            }
        } else {
            for (TilePair t : mViewModel.getCurrentRow()) {
                View k = key[keyLookup(t.getC())];
                int d = (int) k.getTag();
                int newTag = TilePair.UNCHECKED;
                if (t.getD() == TilePair.CORRECT) {
                    newTag = TilePair.CORRECT;
                } else if (t.getD() == TilePair.MISPLACED) {
                    if (d != TilePair.CORRECT) {
                        newTag = TilePair.MISPLACED;
                    }
                } else if (t.getD() == TilePair.INCORRECT) {
                    if (d == TilePair.UNCHECKED) {
                        newTag = TilePair.INCORRECT;
                    }
                }
                if (newTag != d) {
                    k.setBackground(AppCompatResources.getDrawable(requireContext(), newTag));
                    k.setTag(newTag);
                }
            }
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
}
