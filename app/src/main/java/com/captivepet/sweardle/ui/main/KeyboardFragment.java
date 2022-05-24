package com.captivepet.sweardle.ui.main;

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

import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.captivepet.sweardle.CustomImageButton;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;
import java.util.Locale;

/**
 * Builds the keyboard which never changes
 */
public class KeyboardFragment extends Fragment {

    private MainViewModel mViewModel;
    private ConstraintLayout mLayout;
    private int keyHeight;
    private int keyWidth;
    private int keyMargin;
    private View[] key;

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
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getSignal().observe(getViewLifecycleOwner(), item -> {
            updateKeys();
        });

        mLayout = (ConstraintLayout) view;
    }

    public int init(Size s) {
        int mWidth = s.getWidth();
        int mHeight = s.getHeight();
        int keyboardWidth, keyboardHeight, gameboardHeight;
        int PORTRAIT = 0;
        int LANDSCAPE = 1;
        int keyboardOrientation;
        if (mHeight > mWidth) {
            keyboardOrientation = PORTRAIT;
            keyboardWidth = mWidth;
            // total height of kb is at least 1/3 of screen
            keyboardHeight = Math.max((int)(mHeight * 0.333f), keyboardWidth / 4 + keyMargin * 3);
            keyWidth = (mWidth - 4 * keyMargin) / 10;
            keyHeight = Math.min((keyboardHeight - keyMargin * 3) / 3, (3 * keyWidth) / 2);
            gameboardHeight = Math.min(mWidth, mHeight - keyboardHeight);
        } else {
            gameboardHeight = mHeight;
            keyboardWidth = mWidth / 2;
            keyboardOrientation = LANDSCAPE; //TODO
        }
        keyMargin = 2;
        // make the keys
        char[] keyLabel = MainViewModel.KEY_LABEL;
        key = new View[keyLabel.length];
        for (int ix = 0; ix < 28; ix++) {
            View k;
            if (keyLabel[ix] == MainViewModel.ENTER) {
                k = new CustomImageButton(mLayout.getContext(), keyLabel[ix]);
                CustomImageButton kcb = (CustomImageButton) k;
                kcb.setImageResource(R.drawable.ic_baseline_keyboard_return_24);
            } else if (keyLabel[ix] == MainViewModel.DEL) {
                k = new CustomImageButton(mLayout.getContext(), keyLabel[ix]);
                CustomImageButton kcb = (CustomImageButton) k;
                kcb.setImageResource(R.drawable.ic_baseline_undo_24);
            } else {
                k = new Button(mLayout.getContext());
                ((Button) k).setMaxLines(1);
                ((Button) k).setGravity(Gravity.CENTER);
                ((Button) k).setText(String.format(Locale.US, "%c", keyLabel[ix]));
            }
            k.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
            mLayout.addView(k);
            k.setId(View.generateViewId());
            k.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewModel.onClick(view);
                }
            });
            key[ix] = k;
        }
        // position the keys
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < 28; ix++) {
            View k = key[ix];
            int id = k.getId();
            // set key widths & text
            if (keyLabel[ix] == MainViewModel.ENTER) {
                set.constrainWidth(id, (int) (1.5f * keyWidth));
            } else if (keyLabel[ix] == MainViewModel.DEL)  {
                ((CustomImageButton) k).setImageResource(R.drawable.ic_baseline_undo_24);
                set.constrainWidth(id, (int) (1.5f * keyWidth));
            } else {
                set.constrainWidth(id, keyWidth);
            }
            set.constrainHeight(id, keyHeight);
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;
            // top & bottom constraints
            if (ix == 0) {
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
                topM = keyHeight / 4;
                bottom = key[10].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = keyMargin;
            } else if (ix == 10) {
                top = key[0].getId();
                topS = ConstraintSet.BOTTOM;
                topM = 0;
                bottom = key[19].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = keyMargin;
            } else if (ix == 19) {
                top = key[10].getId();
                topS = ConstraintSet.BOTTOM;
                topM = keyMargin;
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
                bottomM = keyHeight / 4;
            } else {
                top =  key[ix - 1].getId();
                topS = ConstraintSet.TOP;
                topM = 0;
                bottom = key[ix - 1].getId();
                bottomS = ConstraintSet.BOTTOM;
                bottomM = keyMargin;
            }
            // left & right constraints
            if (ix == 0 || ix == 10 || ix == 19) {
                set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                left = ConstraintSet.PARENT_ID;
                leftS = ConstraintSet.LEFT;
                leftM = 0;
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            } else if (ix == 9 || ix == 18 || ix == 27) {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = 0;
                right = ConstraintSet.PARENT_ID;
                rightS = ConstraintSet.RIGHT;
                rightM = 0;
            } else {
                left = key[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = 0;
                right = key[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = keyMargin;
            }
            set.connect(id, ConstraintSet.TOP, top, topS, topM);
            set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, bottomM);
            set.connect(id, ConstraintSet.LEFT, left, leftS, leftM);
            set.connect(id, ConstraintSet.RIGHT, right, rightS, rightM);
        }
        set.applyTo(mLayout);
        return(gameboardHeight);
    }

    private void updateKeys() {
        for (int ix = 0; ix < key.length; ix++) {
            for (TilePair t : mViewModel.getCurrentRow()) {
                Button k;
                if (key[ix] instanceof Button) {
                    k = (Button) key[ix];
                    if (k.getText().charAt(0) == t.getC()) {
                        k.setBackground(AppCompatResources.getDrawable(requireContext(), t.getD()));
                        break;
                    }
                }
            }
        }
    }
}