package com.captivepet.sweardle.ui.main;

import static com.captivepet.sweardle.R.id.fragment_game;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Point;
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
import android.widget.Button;
import android.widget.Toast;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;


public class GameFragment extends Fragment {

    private Point size;
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

    private boolean GUESS_TESTED;

    // LiveData signals
    public static final String ADD_CHAR = "Add one char";
    public static final String WINNER = "Winner";
    public static final String LOSER = "Loser";
    public static final String ROW_UPDATED = "Re-highlight row";
    public static final String RESET = "Clear all";
    public static final String DEL = "" + KeyboardFragment.DEL;

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
        mViewModel.getGameSignal().observe(getViewLifecycleOwner(), this::onSignal);
        mainToolbar = requireActivity().findViewById(R.id.main_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
    }

    @Override
    public void onResume() {
        // called this way to ensure that KeyboardFragment as finished init()
        View parent = (View) mLayout.getParent();
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mViewModel.getPosition() == 0) {
                    mViewModel.newGame();

                }
                init(Math.min(size.x, size.y));
                updateAllTiles();
            }
        });
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

    public void onSignal(String signal) {
        switch (signal) {
            case BAD_WORD:

                break;
            case WINNER:

                break;
            case LOSER:

                break;
            default:
                processChar(signal);
        }
    }

    private void processChar(String s) {
        if (s.length() != 1) {
            return;
        }
        char keyChar = s.charAt(0);
        switch(keyChar) {
            case KeyboardFragment.ENTER:
                if (mViewModel.getCurrentRow().size() == WORD_LENGTH && !GUESS_TESTED) {
                    if (mViewModel.validateGuess()) {
                        boolean WINNER = mViewModel.testWord();
                        GUESS_TESTED = true;
                        for (int ix = mViewModel.getPosition() - WORD_LENGTH; ix < mViewModel.getPosition(); ix++) {
                            setTile(ix, mViewModel.get(ix).getChar(), mViewModel.get(ix).getStatus());
                        }
                        mViewModel.getKeyboardSignal().setValue(KeyboardFragment.UPDATE_FROM_LAST_ROW);
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
                if (mViewModel.getPosition() > 0 && mViewModel.getLastChar().getStatus() == TilePair.UNCHECKED) {
                    mViewModel.deleteLastChar(); // position - 1
                    setTile(mViewModel.getPosition(), EMPTY, TilePair.UNCHECKED);
                }
                break;
            default:
                if (KeyboardFragment.firstLetter <= keyChar && keyChar<= KeyboardFragment.lastLetter) {
                    setTile(mViewModel.getPosition(), keyChar, TilePair.UNCHECKED);
                    mViewModel.addChar(keyChar);
                    GUESS_TESTED = false;
                }
        }
    }

    public void init(int height) {
        int tileSize = height / (WORD_LENGTH + TILE_WIDTH_ADJUST);
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

    public void updateAllTiles() {
        int last = mViewModel.getPosition();
        for (int ix = 0; ix < last; ix++) {
            TilePair tp = mViewModel.get(ix);
            setTile(ix, tp.getChar(), tp.getStatus());
        }
        for (int ix = last; ix < WORD_LENGTH * ROW_COUNT; ix++) {
            setTile(ix, GameFragment.EMPTY, TilePair.UNCHECKED);
        }
    }

    public void setTile(int ix, char c, int d) {
        if (tile == null || tile[ix] == null) {
            return;
        }
        tile[ix].setText(String.format("%c", c));
        tile[ix].setBackground(AppCompatResources.getDrawable(requireContext(), d));
        rowUpdated = false;
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
        updateAllTiles();
        mViewModel.getKeyboardSignal().setValue(RESET);
    }
}