package com.captivepet.sweardle.ui.main;
import static com.captivepet.sweardle.R.id.fragment_game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProvider;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private Toolbar mainToolbar;
    private final Button[] tile = new Button[ROW_COUNT * WORD_LENGTH];
    private final int TILE_MARGIN = 1;
    private final int TILE_WIDTH_ADJUST = 2;
    public final static int ROW_COUNT = 6;
    public final static int WORD_LENGTH = 5;
    public final static char EMPTY = ' ';
    public static final String BAD_WORD = "Guess not in dict";

    // LiveData signals
    public static final String ADD_CHAR = "Add one char";
    public static final String WINNER = "Winner";
    public static final String LOSER = "Loser";
    public static final String ROW_UPDATED = "Re-highlight row";
    public static final String RESET = "Clear all";
    public static final String DEL = "" + Keyboard.DEL;

    public boolean rowUpdated;


    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = view.findViewById(fragment_game);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGameSignal().observe(getViewLifecycleOwner(), this::signalProcessor);
        mainToolbar = requireActivity().findViewById(R.id.main_toolbar);
        // called this way to ensure that KeyboardFragment as finished init()
        View parent = (View) mLayout.getParent();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Keyboard k = new Keyboard(requireContext());
                Point size = getDisplayContentSize();
                k.setSize(size);
                k.setmViewModel(mViewModel);
                k.generateKeyboardBottomRight(mLayout);
                init(size);
                mViewModel.newGame();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                this.newGame();
                return true;
            case R.id.statistics:
                this.showStatistics();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void signalProcessor(String signal) {
        if (BAD_WORD.equals(signal)) {
            badWordAlert();
        } else if (RESET.equals(signal)) {
            resetTiles();
        } else if (WINNER.equals(signal)) {
            newGameQuery(getString(R.string.congratulations));
        } else if (LOSER.equals(signal)) {
            newGameQuery(String.format(getString(R.string.taunt), mViewModel.getGameWord()));
        } else {
            ArrayList<TilePair> row = mViewModel.getCurrentRow(); //(ArrayList<TilePair>) mViewModel.getCurrentRow().clone();
            int startOfRow = mViewModel.getRowsDone() * WORD_LENGTH;
            if (ADD_CHAR.equals(signal)) {
                setTile(startOfRow + row.size() - 1,
                            row.get(row.size() - 1).getC(),
                            AppCompatResources.getDrawable(requireContext(), row.get(row.size() - 1).getD())
                );
            } else if (ROW_UPDATED.equals(signal)) {
                for (int ix = 0; ix < WORD_LENGTH; ix++) { // redraw row to set highlights
                    char c = row.get(ix).getC();
                    Drawable d = AppCompatResources.getDrawable(requireContext(), row.get(ix).getD());
                    setTile(startOfRow + ix, c, d);
                }
            } else if (DEL.equals(signal)) {
                setTile(startOfRow + row.size(), EMPTY, AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
            }
        }
    }

    public void init(Point size) {

        Guideline endG = new Guideline(requireContext());
        int endId = View.generateViewId();
        endG.setId(endId);
        Guideline bottomG = new Guideline(requireContext());
        int bottomGId = View.generateViewId();
        bottomG.setId(bottomGId);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.orientation = ConstraintLayout.LayoutParams.HORIZONTAL;
        bottomG.setLayoutParams(params);
        bottomG.setGuidelinePercent(0.5f); // portrait mode, top of keyboard is middle of screen
        params.orientation = ConstraintLayout.LayoutParams.VERTICAL;
        endG.setLayoutParams(params);
        endG.setGuidelinePercent(0.0f); // portrait mode, left edge of keyboard is left edge of screen
        int tileSize = size.x / (WORD_LENGTH + TILE_WIDTH_ADJUST);
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
        for (int ix = 0; ix < tile.length; ix++) {
            Button k = tile[ix];
            k.setEnabled(false);

            int id = k.getId();
            k.setGravity(Gravity.CENTER);
            // set tile widths
            set.constrainWidth(id, tileSize);
            set.constrainHeight(id, tileSize);
            set.constrainMaxHeight(id, tileSize);
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;
            // top & bottom constraints

            if (ix == 0) {
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
                topM = tileSize / 2;
                bottom = tile[ix + WORD_LENGTH].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = TILE_MARGIN;
            } else if (ix == (ROW_COUNT - 1) * WORD_LENGTH) {
                top = tile[ix - WORD_LENGTH].getId();
                topS = ConstraintSet.BOTTOM;
                topM = TILE_MARGIN;
                bottom = bottomGId;
                bottomS = ConstraintSet.TOP;
                bottomM = tileSize / 2;
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
                right = endId;
                rightS = ConstraintSet.LEFT;
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
//        set.applyTo(mLayout);
    }

    public void resetTiles() {
        for (int ix = 0; ix < tile.length; ix++) {
            setTile(ix, GameFragment.EMPTY, AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
        }
        rowUpdated = false;
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
            (dialog, id) -> {
                resetTiles();
                mViewModel.getKeyboardSignal().setValue(RESET);
                mViewModel.newGame();
            });
        builder.setNegativeButton(getString(R.string.cancel),
            (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().verticalMargin = tile[0].getHeight() / 2;
        dialog.show();
    }

    // https://www.javatpoint.com/android-alert-dialog-example
    private void badWordAlert() {
        Toast t = Toast.makeText(requireContext().getApplicationContext(),
                "Sorry, I don't know that word", Toast.LENGTH_LONG);
        t.show();
    }

    public void showStatistics() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Statistics");
        builder.setMessage("You're doing great!");
        builder.setPositiveButton("Close",
            (dialog, id) -> {
                dialog.dismiss();
            });
    }

    public void newGame() {
        mViewModel.newGame();
    }


    // https://gist.github.com/dominicthomas/8257203
    public Point getDisplayContentSize() {
        final WindowManager windowManager = requireActivity().getWindowManager();
        final Point size = new Point();
        int screenHeight = 0, actionBarHeight = 0;
        if (requireActivity().getActionBar() != null) {
            actionBarHeight = requireActivity().getActionBar().getHeight();
        }
        int contentTop = ((ViewGroup) requireActivity().findViewById(android.R.id.content)).getTop();
        windowManager.getDefaultDisplay().getSize(size);
        size.y -= (contentTop + actionBarHeight);
        return size;
    }
}