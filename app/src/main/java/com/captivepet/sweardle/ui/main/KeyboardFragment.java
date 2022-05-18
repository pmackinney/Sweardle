package com.captivepet.sweardle.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.captivepet.sweardle.MainActivity;
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

    int mHeight;
    int mWidth;
    int keyHeight;
    int keyWidth;
    int keyMargin;
    final int PORTRAIT=2;
    final int LANDSCAPE=1;
    int keyboardOrientation;
    int ENTER=19;
    int DEL=27;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mLayout = (ConstraintLayout) view;
        setDimensions(view);
        ((TextView) ((MainActivity) getContext()).findViewById(R.id.fragmentMessage)).setVisibility(View.GONE);
        createKeys(mLayout);
//        mLayout.forceLayout();
    }

    private void setDimensions(View view) {
        String[] size = ((MainActivity) this.getContext()).getSize().split("x");
        mWidth = Integer.parseInt(size[0]);
        mHeight = Integer.parseInt(size[1]);
        if (mHeight > mWidth) {
            keyboardOrientation = PORTRAIT;
        } else {
            keyboardOrientation = LANDSCAPE;
        }
        keyWidth = (int) (Math.min(mWidth, mHeight) / 12);
        keyHeight = (int) (keyWidth * 1.3f);
        keyMargin = (int) (Math.max(1, 0.1f * keyWidth));
    }

    protected void createKeys(View view) {
        // make all the keys
        char[] letters = new char[]{'Q', 'W', 'E', 'R',  'T','Y', 'U', 'I', 'O', 'P',
                'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
                '+', 'Z', 'X', 'C','V', 'B','N', 'M', '-'};
        Button[] key = new Button[letters.length];
        for (int ix = 0; ix < 28; ix++) {
            Button k = new Button(view.getContext());
            mLayout.addView(k);
            k.setId(View.generateViewId());
            key[ix] = k;
        }
        ConstraintSet set = new ConstraintSet();
        set.clone((ConstraintLayout) view);
        for (int ix = 0; ix < 28; ix++) {
            Button k = key[ix];
            int id = k.getId();
            set.constrainWidth(id, keyWidth);
            set.constrainHeight(id, keyHeight);
            k.setText(String.format(Locale.US, "%c", letters[ix]));
            k.setGravity(Gravity.CENTER);
            int top = -1;
            int left = -1;
            int right = -1;
            int bottom = -1;
            int topS = -1;
            int leftS = -1;
            int rightS = -1;
            int bottomS = -1;
            if (ix < 10) {
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
//                bottom = key[10].getId();
//                bottomS = ConstraintSet.TOP;
//                bottomM = keyMargin;
                bottom = -1;
                bottomS = -1;
                //set.setVerticalBias(keyId[ix], 1.0f);
            } else if (ix < 19) {
                top = key[0].getId();
                topS = ConstraintSet.BOTTOM;
//                bottom = key[10].getId();
//                bottomS = ConstraintSet.TOP;
//                bottomM = keyMargin;
                bottom = -1;
                bottomS = -1;
            } else {
                top = key[10].getId();
                topS = ConstraintSet.BOTTOM;
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
                set.setVerticalBias(key[ix].getId(), 0f);
            }
            if (ix == 0 || ix == 10 || ix == 19) {
                left = ConstraintSet.PARENT_ID;
                leftS = ConstraintSet.LEFT;
            } else {
                left = -1;
                leftS = -1;
//                start = key[ix - 1].getId();
//                startS = ConstraintSet.END;
            }
            if (ix == 9 || ix == 18 || ix == 27) {
                right = ConstraintSet.PARENT_ID;
                rightS = ConstraintSet.RIGHT;
            } else {
                right = -1;
                rightS = -1;
//                end = key[ix + 1].getId();
//                endS = ConstraintSet.START;
            }
            set.constrainWidth(id, keyWidth);
            set.constrainHeight(id, keyHeight);
            if (top != -1) {
                set.connect(id, ConstraintSet.TOP, top, topS);
            } else {
                set.clear(id, ConstraintSet.TOP);
            }
            if (bottom != -1) {
                set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, keyMargin);
            } else {
                set.clear(id, ConstraintSet.BOTTOM);
            }
            if (left != -1) {
                set.connect(id, ConstraintSet.LEFT, left, leftS);
            } else {
                set.clear(id, ConstraintSet.LEFT);
            }
            if (right != -1) {
                set.connect(id, ConstraintSet.RIGHT, right, rightS);
            } else {
                set.clear(id, ConstraintSet.RIGHT);
            }
            // set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
            set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
        }

        // set special keys

        key[ENTER].setText("ENTER");
        set.constrainWidth(key[ENTER].getId(), 2 * keyWidth);
        key[DEL].setText("DEL");
        set.constrainWidth(key[DEL].getId(), 2 * keyWidth);

        set.applyTo(mLayout);
    }
}