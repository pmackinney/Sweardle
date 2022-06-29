package com.captivepet.sweardle.ui.main;

import static android.content.ContentValues.TAG;
import static com.captivepet.sweardle.R.id.fragment_game;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

import java.util.List;


public class GameFragment extends Fragment implements LifecycleOwner {
    private final String tag = this.getClass().getSimpleName();

    private Point size;
    private ConstraintLayout mLayout;
    private MainViewModel mViewModel;
    private Toolbar mainToolbar;
    private final AppCompatTextView[] tile = new AppCompatTextView[ROW_COUNT * WORD_LENGTH];
    private final int TILE_MARGIN = 1;
    private final int TILE_WIDTH_ADJUST = 2;
    public final static int ROW_COUNT = 6;
    public final static int WORD_LENGTH = 5;
    public final static char EMPTY = ' ';
    public static final String BAD_WORD = "Guess not in dict";

    private boolean GUESS_TESTED;

    // LiveData signals
    public static final String ADD_CHAR = "Add one char";
    public static final String WINNER = "Winner";
    public static final String LOSER = "Loser";
    public static final String ROW_UPDATED = "Re-highlight row";
    public static final String RESET = "Clear all";
    public static final String DEL = "" + KeyboardFragment.DEL;

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
        mViewModel.getGameboard().observe(getViewLifecycleOwner(), this::onChanged);
        mainToolbar = requireActivity().findViewById(R.id.main_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
    }

    @Override
    public void onResume() {
//        View parent = (View) mLayout.getParent();
//        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                if (mViewModel.getPosition() == 0) {
//                    mViewModel.newGame();
//
//                }
//                init(Math.min(size.x, size.y));
//            }
//        });
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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

    public void setSize(Point s) {
        this.size = s;
    }

    /**
     * Redraw the last row of the gameboard
     * @param pairList - LiveData object that triggers this method
     */
    public void onChanged(List<TilePair> pairList) {
        int position = pairList.size();
        if (position == 0) {
            for (int ix = 0; ix < tile.length; ix++) {
                tile[ix] = getDefaultTile(tile[ix]);
            }
        }
        int start = (position / WORD_LENGTH) * WORD_LENGTH;
        if (start > 0 && position % WORD_LENGTH == 0) {
            start -= WORD_LENGTH;
        }
        for (int ix = start; ix < position; ix++) {
            if (tile[ix] == null) { // e.g., after rotation
                return;
            }
            tile[ix].setText(String.format("%c", mViewModel.getTileChar(ix)));
            tile[ix].setBackground(AppCompatResources.getDrawable(requireContext(), mViewModel.getTileStatus(ix)));
        }
        if (position < WORD_LENGTH * ROW_COUNT) {
            tile[position] = getDefaultTile(tile[position]);
        }
    }

    public void processChar(char keyChar) {
        switch(keyChar) {
        case KeyboardFragment.ENTER:
            List<TilePair> guess = mViewModel.getLastRow(); // valid word or null
            if (guess == null || guess.size() < WORD_LENGTH) {
                return;
            }
            if (!GUESS_TESTED) {
                if (mViewModel.validateGuess()) { // word must be in dict
                    boolean WINNER = mViewModel.testWord();
                    GUESS_TESTED = true;
                    if (WINNER) {
                        newGameQuery(getString(R.string.congratulations));
                    } else if (mViewModel.getRowsDone() == ROW_COUNT) {
                        newGameQuery(String.format(getString(R.string.taunt), mViewModel.getSolution()));
                    }
                } else {
                    badWordAlert();
                }
            }
            break;
        case KeyboardFragment.DEL:
            TilePair tp = mViewModel.getLastChar();
            if (tp != null && tp.getStatus() == TilePair.UNCHECKED) {
                Log.d(TAG, String.format("tp = %c, %s", tp.getChar(), TilePair.getStatusName(tp.getStatus())));
                mViewModel.deleteLastChar();
                if (mViewModel.getPosition() % WORD_LENGTH == 0) {
                    GUESS_TESTED = true;
                }
            }
            break;
        default:
            if (KeyboardFragment.firstLetter <= keyChar && keyChar<= KeyboardFragment.lastLetter) {
                if (GUESS_TESTED || mViewModel.getPosition() == 0 || mViewModel.getPosition() % WORD_LENGTH != 0) {
                    mViewModel.addChar(keyChar);
                    GUESS_TESTED = false;
                }
            }
        }
    }
    int counter = 0;
    private AppCompatTextView getDefaultTile(AppCompatTextView tv) {
        Log.d(TAG, "getDefaultTile");
        if (tv == null) {
            Log.d(TAG, "creating tv " + counter++);
            tv = new AppCompatTextView(mLayout.getContext());
            mLayout.addView(tv);
            tv.setId(View.generateViewId());
            tv.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.black));
        }
        tv.setBackground(AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
        tv.setText(String.valueOf(KeyboardFragment.BLANK));
        return tv;
    }

    public void init(int height) {
        Log.d(TAG, "init");
        int tileSize = height / (WORD_LENGTH + TILE_WIDTH_ADJUST);
        // make all the tiles;
        for (int ix = 0; ix < ROW_COUNT * WORD_LENGTH; ix++) {
            tile[ix] = getDefaultTile(tile[ix]);
        }
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < tile.length; ix++) {
            AppCompatTextView k = tile[ix];
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
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
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
    }

    // https://www.javatpoint.com/android-alert-dialog-example
    private void newGameQuery(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.game_over));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.new_game),
            (dialog, id) -> {
                newGame();
            });
        builder.setNegativeButton(getString(R.string.cancel),
            (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().verticalMargin = tile[0].getHeight() / 2f;
        dialog.show();
    }

    // https://www.javatpoint.com/android-alert-dialog-example
    private void badWordAlert() {
        Toast t = Toast.makeText(requireContext().getApplicationContext(),
                "Sorry, I don't know that word", Toast.LENGTH_LONG);
        t.show();
    }

    public void showStatistics() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Statistics");
        builder.setMessage("You're doing great!");
        builder.setPositiveButton("Close",
            (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().verticalMargin = tile[0].getHeight() / 2f;
        dialog.show();
    }

    public void newGame() {
        mViewModel.newGame();
    }
}