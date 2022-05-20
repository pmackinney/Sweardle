package com.captivepet.sweardle.ui.main;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import com.captivepet.sweardle.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment {

    private MainViewModel mViewModel;
    private ConstraintLayout mLayout;
    private char[] letters;
    int mHeight;
    int mWidth;
    int keyHeight;
    int keyWidth;
    int keyMargin;
    final int PORTRAIT=2;
    final int LANDSCAPE=1;
    int keyboardOrientation;

    public KeyboardFragment() {
        // Required empty public constructor ??
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment KeyboardFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static KeyboardFragment newInstance(String param1, String param2) {
//        KeyboardFragment fragment = new KeyboardFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public static KeyboardFragment newInstance() {
        return new KeyboardFragment();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState
//    ) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_keyboard, container, false);
//    }
//}
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
        mLayout = (ConstraintLayout) view;
    }

    private void setDimensions(Size size) {
        mWidth = size.getWidth();
        mHeight = size.getHeight();
        if (mHeight > mWidth) {
            keyboardOrientation = PORTRAIT;
        } else {
            keyboardOrientation = LANDSCAPE;
        }
        keyWidth = (int) (Math.min(mWidth, mHeight) / 12);
        keyHeight = (int) (keyWidth * 1.3f);
//        keyMargin = (int) (Math.min(1, 0.1f * keyWidth));
        keyMargin = 1;
    }

    public void init(Size s) {
        letters = MainViewModel.LETTERS;
        setDimensions(s);
        // make all the keys
        View[] key = new View[letters.length];
        for (int ix = 0; ix < 28; ix++) {
            View k;
            if (ix == MainViewModel.ENTER || ix == MainViewModel.DEL) {
                k = new CustomImageButton(mLayout.getContext());
                ((CustomImageButton) k).setText(String.format(Locale.US, "%c", letters[ix]));
            } else {
                k = new Button(mLayout.getContext());
                ((Button) k).setMaxLines(1);
                ((Button) k).setGravity(Gravity.CENTER);
                ((Button) k).setText(String.format(Locale.US, "%c", letters[ix]));
            }
            mLayout.addView(k);
            k.setId(View.generateViewId());
            k.setOnClickListener(item -> {
                mViewModel.keyTap(k);
            });
            key[ix] = k;
        }
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < 28; ix++) {
            View k = key[ix];
            int id = k.getId();
            k.setBackground(getResources().getDrawable(R.drawable.frame_grey, null));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                k.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_NONE);
//            }
//            set.setVerticalBias(id, 0.5);
//            set.setHorizontalBias(id,0.5);


            // set key widths & text
            if (ix == MainViewModel.ENTER) {

                set.constrainWidth(id, 2 * keyWidth);
            } else if  (ix == MainViewModel.DEL)  {

                set.constrainWidth(id, 2 * keyWidth);
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
                topM = keyHeight / 2;
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
                bottomM = keyHeight / 2;
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
    }
}