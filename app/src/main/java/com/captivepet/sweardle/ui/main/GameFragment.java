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
import android.widget.Button;
import android.widget.Toast;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;

import java.util.List;


public class GameFragment extends Fragment {

    private int tileSize;
    private float guideLinePercentage;
    private boolean IS_PORTRAIT;
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
    public static final String DEL = "" + KeyboardFragment.DEL;
    public static final String INIT = "Initialize";

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
        mViewModel.getGameSignal().observe(getViewLifecycleOwner(), this::signalReceived);
        mainToolbar = requireActivity().findViewById(R.id.main_toolbar);
//        mainToolbar.setTitleTextColor(R.color.white);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);

//        // called this way to ensure that KeyboardFragment as finished init()
//        View parent = (View) mLayout.getParent();
//        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//            }
//        });
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

    public void signalReceived(String signal) {
        if (BAD_WORD.equals(signal)) {
            badWordAlert();
        } else if (RESET.equals(signal)) {
            resetTiles();
        } else if (WINNER.equals(signal)) {
            newGameQuery(getString(R.string.congratulations));
        } else if (LOSER.equals(signal)) {
            newGameQuery(String.format(getString(R.string.taunt), mViewModel.getGameWord()));
        } else if (INIT.equals(signal.substring(INIT.length()))) {
            float pkg = Float.parseFloat(signal.substring(INIT.length()));
            setSizes(pkg);
            init();
        } else {
            List<TilePair> row = mViewModel.getCurrentRow(); //(ArrayList<TilePair>) mViewModel.getCurrentRow().clone();
            int startOfRow = mViewModel.getRowsDone() * WORD_LENGTH;
            if (ADD_CHAR.equals(signal)) {
                setTile(startOfRow + row.size() - 1,
                            row.get(row.size() - 1).getChar(),
                            AppCompatResources.getDrawable(requireContext(), row.get(row.size() - 1).getStatusId())
                );
            } else if (ROW_UPDATED.equals(signal)) {
                for (int ix = 0; ix < WORD_LENGTH; ix++) { // redraw row to set highlights
                    char c = row.get(ix).getChar();
                    Drawable d = AppCompatResources.getDrawable(requireContext(), row.get(ix).getStatusId());
                    setTile(startOfRow + ix, c, d);
                }
            } else if (DEL.equals(signal)) {
                setTile(startOfRow + row.size(), EMPTY, AppCompatResources.getDrawable(requireContext(), TilePair.UNCHECKED));
            }
        }
    }

    public void setSizes(float sizePkg) {
        tileSize = KeyboardFragment.getSizeFromPkg(sizePkg) / (WORD_LENGTH + TILE_WIDTH_ADJUST);
        guideLinePercentage = KeyboardFragment.getPercentageFromPkg(sizePkg);
        IS_PORTRAIT = KeyboardFragment.isPkgPortrait(sizePkg);
    }

    public void init() {
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

        Guideline fGuideline = new Guideline(requireContext());
        int floor = View.generateViewId();
        fGuideline.setId(floor);
        mLayout.addView(fGuideline);
        set.create(floor, ConstraintSet.HORIZONTAL_GUIDELINE);
        float percentage = IS_PORTRAIT ? guideLinePercentage : 0.0f;
        set.setGuidelinePercent(floor, guideLinePercentage);

        Guideline rGuideline = new Guideline(requireContext());
        int rightWall = View.generateViewId();
        rGuideline.setId(rightWall);
        mLayout.addView(rGuideline);
        set.create(rightWall, ConstraintSet.VERTICAL_GUIDELINE);
        percentage = IS_PORTRAIT ? 1.0f : guideLinePercentage;
        set.setGuidelinePercent(rightWall, percentage);

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
                bottom = floor;
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
                right = rightWall;
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
        set.applyTo(mLayout);
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

    //  // Theme_AppCompat_DayNight_Dialog
    // AlertDialog_AppCompat
    // Base_ThemeOverlay_AppCompat_Dialog_Alert
    // https://www.javatpoint.com/android-alert-dialog-example
    private void newGameQuery(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
        resetTiles();
        mViewModel.getKeyboardSignal().setValue(RESET);
        mViewModel.newGame();
    }
}