package com.captivepet.sweardle.ui.main;
import static com.captivepet.sweardle.R.id.fragment_game;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    private ConstraintLayout mLayout;
    private MainViewModel mViewModel;
    int tileWidth;
    public static final String READY = "Ready";
    public static final String WINNER = "Winner";
    public static final String ROW_UPDATED = "ready for keyboard update";
    public static final String RESET_KEYS = "Set all keys to UNTESTED";
    public boolean rowUpdated;
    public final static int ROW_COUNT = 6;
    public final static int WORD_LENGTH = 5;
    public final static char EMPTY = ' ';

    final Button[] tile = new Button[ROW_COUNT * WORD_LENGTH];
    final int TILE_MARGIN = 1;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = view.findViewById(fragment_game);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGameSignal().observe(getViewLifecycleOwner(), this::updateRow);

        View parent = (View) mLayout.getParent();
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mViewModel.newGame();
            }
        });
    }

    public void updateRow(String signal) {
        if ("Bad word".equals(signal)) {
            badWordAlert();
            return;
        }
        ArrayList<TilePair> row = (ArrayList<TilePair>) mViewModel.getCurrentRow().clone();
        int rowsDone = mViewModel.getRowsDone();
        if (mViewModel.getTESTED()) {
            mViewModel.newRow();
        }
        int nextChar = row.size();
        int startOfRow = rowsDone * WORD_LENGTH;
        for (int ix = 0; ix < row.size(); ix++) {
            char c = row.get(ix).getC();
            Drawable d = AppCompatResources.getDrawable(requireContext(), row.get(ix).getD());
            setTile(startOfRow + ix, c, d);
        }
        if (row.size() < WORD_LENGTH) { // handle backspace
            setTile(startOfRow + nextChar, EMPTY, AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
        }
        if (mViewModel.getWinner()) {
            newGameQuery(getString(R.string.congratulations));
        } else if (mViewModel.getRowsDone() == ROW_COUNT) {
            newGameQuery(String.format(getString(R.string.taunt), mViewModel.getGameWord()));
        }
    }

    public void init(int height) {
        tileWidth = height / (WORD_LENGTH + 1);
        // make all the tiles;
        for (int ix = 0; ix < ROW_COUNT * WORD_LENGTH; ix++) {
            Button k = new Button(mLayout.getContext());
            mLayout.addView(k);
            k.setId(View.generateViewId());
            k.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.black));
            tile[ix] = k;
        }
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < ROW_COUNT * WORD_LENGTH; ix++) {
            Button k = tile[ix];
            k.setEnabled(false);

            int id = k.getId();
            k.setGravity(Gravity.CENTER);
            // set tile widths
            set.constrainWidth(id, tileWidth);
            set.constrainHeight(id, tileWidth);
            set.constrainMaxHeight(id, tileWidth);
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;
            // top & bottom constraints
            if (ix == 0) {
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
                topM = tileWidth / 2;
                bottom = tile[ix + WORD_LENGTH].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = TILE_MARGIN;
            } else if (ix == (ROW_COUNT - 1) * WORD_LENGTH) {
                top = tile[ix - WORD_LENGTH].getId();
                topS = ConstraintSet.BOTTOM;
                topM = TILE_MARGIN;
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
                bottomM = tileWidth / 2;
            } else if (ix % WORD_LENGTH == 0) {
                top = tile[ix - WORD_LENGTH].getId();
                topS = ConstraintSet.BOTTOM;
                topM = TILE_MARGIN;
                bottom = tile[ix + WORD_LENGTH].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = TILE_MARGIN;
            } else {
                top = tile[ix - 1].getId();
                topS = ConstraintSet.TOP;
                topM = 0;
                bottom = tile[ix - 1].getId();
                bottomS = ConstraintSet.BOTTOM;
                bottomM = 0;
            }
            // left & right constraints
            if (ix % WORD_LENGTH == 0) {
                set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                left = ConstraintSet.PARENT_ID;
                leftS = ConstraintSet.LEFT;
                leftM = 0;
                right = tile[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = TILE_MARGIN;
            } else if (ix % WORD_LENGTH == WORD_LENGTH - 1) {
                left = tile[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = TILE_MARGIN;
                right = ConstraintSet.PARENT_ID;
                rightS = ConstraintSet.RIGHT;
                rightM = 0;
            } else {
                left = tile[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = TILE_MARGIN;
                right = tile[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = TILE_MARGIN;
            }
            set.connect(id, ConstraintSet.TOP, top, topS, topM);
            set.connect(id, ConstraintSet.BOTTOM, bottom, bottomS, bottomM);
            set.connect(id, ConstraintSet.LEFT, left, leftS, leftM);
            set.connect(id, ConstraintSet.RIGHT, right, rightS, rightM);
        }
        set.applyTo(mLayout);
        newGame();
    }

    public void newGame() {
        for (int ix = 0; ix < tile.length; ix++) {
            setTile(ix, ' ', AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
        }
        rowUpdated = false;
        mViewModel.newGame();
    }

    public void setTile(int ix, char c, Drawable d) {
        tile[ix].setText(String.format("%c", c));
        tile[ix].setBackground(d);
        rowUpdated = false;
    }

    // https://www.javatpoint.com/android-alert-dialog-example
    private void newGameQuery(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.game_over));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.new_game),
                new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    newGame();
                }
            });
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        AlertDialog dialog = builder.create();
//        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        p.gravity = Gravity.BOTTOM;
        p.verticalMargin = 20;
        dialog.show();
    }

    // https://www.javatpoint.com/android-alert-dialog-example
    private void badWordAlert() {
        Toast t = Toast.makeText(requireContext().getApplicationContext(),
                "Sorry, I don't know that word", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }
}