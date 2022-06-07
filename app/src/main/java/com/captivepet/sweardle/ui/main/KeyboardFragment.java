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
import androidx.lifecycle.ViewModelProvider;

import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;

import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;
import java.util.Locale;

/**
 * Builds the keyboard which never changes
 */
public class KeyboardFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    public static final int PORTRAIT = 1;
    public static final int LANDSCAPE = -1;
    private final Point mSize = new Point();
    private MainViewModel mViewModel;
    private ConstraintLayout mLayout;
    private final int KEY_WIDTH_DIVISOR = 12; // How many keys wide is the full KB width?
    private final int KEY_MARGIN_DIVISOR = 12; // How many margins = 1 key?
    private final float MAX_KEY_HEIGHT_RATIO = 1.2f; // Max keyHeight relative to keyWidth
    private final float SPECIAL_KEY_WIDTH_MULTIPLIER = 1.5f;

    public static final char[] KEY_LABEL = new char[]{
            'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
            'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
            'Z', 'X', 'C', 'V', 'B', 'N', 'M'};
    public static final char[] KEY_LABEL_DVORAK = new char[]{
            'P', 'Y', 'F', 'G', 'C', 'R', 'L',
            'A', 'O', 'E', 'U', 'I', 'D', 'H', 'T', 'N', 'S',
            'Q', 'J', 'K', 'X', 'B', 'M', 'W', 'V', 'Z',};
    public static final char BLANK = ' ';
//    public static final char ENTER = '⏎';
//    public static final char DEL = '⌫';
    public static char ENTER;
    public static char DEL;
    private static final Button[] key = new Button[KEY_LABEL.length];
    private ImageButton enterButton;
    private ImageButton delButton;

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
        mViewModel.getKeyboardSignal().observe(getViewLifecycleOwner(), this::updateKeyboard);
    }

    @Override
    public void onResume() {
        View parent = requireActivity().findViewById(android.R.id.content);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    DisplayMetrics m = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(m);
                    mSize.x = m.widthPixels;
                    mSize.y = m.heightPixels;
                    m.getClass().getSimpleName();
                }
            });
        super.onResume();
    }

    public void setSize(Point p) {
        this.mSize.x = p.x;
        this.mSize.y = p.y;
    }

    private void setGuidelines() {
        int x = 5;
    }
    float gameboardSizePkg;
    public float getGameboardSizePkg() { return gameboardSizePkg; }
    public void setGameboardSizePkg(int size, float percentage, int orientation) {
        gameboardSizePkg = orientation * (size + percentage);
        mViewModel.getGameSignal().setValue(GameFragment.INIT + gameboardSizePkg);
    }
    public static int getSizeFromPkg(float pkg) {
        return Math.abs((int) pkg);
    }
    public static float getPercentageFromPkg(float pkg) {
        return Math.abs(pkg - (int) pkg);
    }
    public static boolean isPkgPortrait(float pkg) {
        return (pkg > 0) ? true : false;
    }

    private String getGameSizePkgString() {
        return String.format(Locale.US, "%f", gameboardSizePkg);
    }
    int keyboardHeight, keyboardWidth, gameSize;
    public void computeSizes(Point size) {

        if (size.y >= size.x) { // portrait
            keyboardWidth = size.x;


        } else { // landscape
            gameSize = (int) Math.min(size.x - size.y, size.x / 2f);
            keyboardWidth = size.x - gameSize;
//            keyWidth = keyboardWidth / KEY_WIDTH_DIVISOR;
//            keyMargin = 1;
//            keyHeight = (int) (MAX_KEY_HEIGHT_RATIO * keyWidth);
//            ceilingPercent = 0.0f;
//            leftWallPercent = gameSize / (float) size.x;
//            setGameboardSizePkg(gameSize, leftWallPercent, LANDSCAPE);
        }
    }

    public void buildKeyboard() {
        // make the keys
        ENTER = getString(R.string.enter_symbol).charAt(0);
        enterButton = new ImageButton(getContext());
        enterButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.frame_highlight));
        enterButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_keyboard_return_24));
        enterButton.setTag(getString(R.string.enter_symbol));
        setCommonProperties(enterButton);

        DEL = getString(R.string.del_symbol).charAt(0);
        delButton = new ImageButton(getContext());
        delButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.frame_highlight));
        delButton.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_baseline_undo_24));
        delButton.setTag(getString(R.string.del_symbol));
        setCommonProperties(delButton);

        for (int ix = 0; ix < key.length; ix++) {
            Button k;
            k = new Button(mLayout.getContext());
            setCommonProperties(k);
            k.setMaxLines(1);
            k.setGravity(Gravity.CENTER);
            k.setText(String.format(Locale.US, "%c", KEY_LABEL[ix]));
            key[ix] = k;
        }

        // set layout size, size & constrain the keys
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        set.constrainHeight(mLayout.getId(), keyboardHeight);
        set.constrainWidth(mLayout.getId(), keyboardWidth);
        int keyWidth = mLayout.getWidth() / KEY_WIDTH_DIVISOR;
        int specialKeyWidth = (int) (SPECIAL_KEY_WIDTH_MULTIPLIER * keyWidth);
        int keyMargin = keyWidth / KEY_MARGIN_DIVISOR;
        int keyHeight = (int) (keyWidth * MAX_KEY_HEIGHT_RATIO);
        int keyboardVerticalMargin = getActivity().findViewById(R.id.main_toolbar).getHeight();

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
                topM = keyboardVerticalMargin;
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
                bottomM = keyboardVerticalMargin;
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
                } else { // ix = 0 or 10
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
        // enter button
        set.connect(enterButton.getId(), ConstraintSet.TOP, key[19].getId(), ConstraintSet.TOP, 0);
        set.connect(enterButton.getId(), ConstraintSet.BOTTOM, key[19].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(enterButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, keyMargin);
        set.connect(enterButton.getId(), ConstraintSet.RIGHT, key[19].getId(), ConstraintSet.LEFT, keyMargin);
        set.setHorizontalChainStyle(enterButton.getId(), ConstraintSet.CHAIN_PACKED);
        set.constrainHeight(enterButton.getId(), keyHeight);        // delete button
        set.constrainWidth(enterButton.getId(), specialKeyWidth);
        set.connect(delButton.getId(), ConstraintSet.TOP, key[25].getId(), ConstraintSet.TOP, 0);
        set.connect(delButton.getId(), ConstraintSet.BOTTOM, key[25].getId(), ConstraintSet.BOTTOM, 0);
        set.connect(delButton.getId(), ConstraintSet.LEFT, key[25].getId(), ConstraintSet.RIGHT, keyMargin);
        set.connect(delButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, keyMargin);
        set.constrainHeight(delButton.getId(), keyHeight);
        set.constrainWidth(delButton.getId(), specialKeyWidth);
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
    public void setCommonProperties(View button) {
        button.setId(View.generateViewId());
        button.setOnClickListener(this::onKeyboard);
        mLayout.addView(button);
    }

    public void onKeyboard(View view) {
        mViewModel.getKeyboardSignal().setValue(((Button) view).getText().toString());
    }

    private void updateKeyboard(@NonNull String signal) {
        if (signal.equals(GameFragment.ROW_CHECKED)) {
            if (enterButton == null) {
                buildKeyboard();
            }
            for (TilePair guess : mViewModel.getCurrentRow()) {
                int guessStatus = guess.getStatusId();
                Button mKey = key[keyLookup(guess.getChar())];
                int keyStatus = (int) mKey.getTag();
                if (keyStatus == TilePair.MISPLACED || keyStatus == TilePair.UNCHECKED) {
                    if (guessStatus == TilePair.CORRECT) {
                        setKeyStatus(mKey, TilePair.CORRECT);
                    } else if (guessStatus == TilePair.MISPLACED && keyStatus != TilePair.MISPLACED) {
                        setKeyStatus(mKey, TilePair.MISPLACED);
                    } else if (guessStatus == TilePair.INCORRECT && keyStatus == TilePair.UNCHECKED) {
                        setKeyStatus(mKey, TilePair.INCORRECT);
                    }
                }
            }
        }
    }

    private void setKeyStatus(Button key, int status) {
        key.setBackground(AppCompatResources.getDrawable(requireContext(), status));
        key.setTag(status);
    }
}
