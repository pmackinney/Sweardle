package com.captivepet.sweardle.ui.main;

import static com.captivepet.sweardle.R.id.fragment_game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

import android.graphics.Rect;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;
import com.captivepet.sweardle.R;
import com.captivepet.sweardle.TilePair;


public class GameFragment extends Fragment {

    private int mWidth;
    private int mHeight;
    private int mWidth1;
    private int mHeight1;
    private int tileSize;
    private final int tileTextColor = R.color.black;
    private boolean IS_PORTRAIT;
    private ConstraintLayout mLayout;
    private MainViewModel mViewModel;
    private Toolbar mainToolbar;
    private final TextView[] tile = new Button[ROW_COUNT * WORD_LENGTH];
    private final int TILE_MARGIN = 1;
    private final int TILE_WIDTH_ADJUST = 2;
    public final static int ROW_COUNT = 6;
    public final static int WORD_LENGTH = 5;
    public final static char EMPTY = ' ';
    public static final String BAD_WORD = "Guess not in dict";
    private boolean WINNER = false;
    private boolean ROW_TESTED;
    private int rowsDone;

    // LiveData signals
    public static final String ADD_CHAR = "Add one char";
    public static final String WON_GAME = "Winner";
    public static final String LOST_GAME = "Loser";
    public static final String ROW_CHECKED = "Re-highlight row";
    public static final String NEW_GAME = "New game";
    public static final String ENTER_KEY = "Enter";
    public static final String DEL_KEY = "Backspace";
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
        mainToolbar = view.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);

        View parent = requireActivity().findViewById(android.R.id.content);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Rect bounds = getActivity().getWindowManager().getCurrentWindowMetrics().getBounds();
                        mWidth = bounds.right - bounds.left;
                        mHeight = bounds.bottom - bounds.top;
                    }
                    mWidth1 = mLayout.getWidth();
                    mHeight1 = mLayout.getHeight();
                }
            });



        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGameSignal().observe(getViewLifecycleOwner(), this::signalReceived);
    }

    @Override
    public void onResume() {
        updateTiles(0);
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
    private void newGame() {
        rowsDone = 0;
        ROW_TESTED = false;
        WINNER = false;
        mViewModel.newGame();
//        updateTiles(0); TOTO
    }

    public void signalReceived(String signal) {
        char keyChar = signal.charAt(0);
        int position = mViewModel.getPosition();
        if ('A' <= keyChar && keyChar <= 'Z' && position < GameFragment.WORD_LENGTH * GameFragment.ROW_COUNT) {
            mViewModel.addChar(keyChar);
            setTile(position, keyChar, TilePair.UNCHECKED);
        } else if (keyChar == KeyboardFragment.DEL && !ROW_TESTED && position - rowsDone * WORD_LENGTH > 0) {
            mViewModel.getGameboard().remove( position - 1);
            setTile(position - 1, EMPTY, TilePair.UNCHECKED);
        } else if (keyChar == KeyboardFragment.ENTER && !ROW_TESTED && rowsDone > 0
                && mViewModel.getPosition() % WORD_LENGTH == 0) {
            if (mViewModel.validateGuess()) {
                WINNER = mViewModel.testWord();
                ROW_TESTED = true;
                rowsDone++;
                mViewModel.getKeyboardSignal().setValue(GameFragment.ROW_CHECKED);
                if (WINNER) {
                    newGameQuery(getString(R.string.congratulations));
                }
                if (!WINNER && (rowsDone == GameFragment.ROW_COUNT)) {
                    newGameQuery(String.format(getString(R.string.taunt), mViewModel.getGameWord()));
                }
            } else {
                badWordAlert();
            }
        } else if (NEW_GAME.equals(signal)) {
            newGame();
        }
    }

    public void setSizes() {
        int myHeight = mLayout.getHeight();
        int myMaxHeight = mLayout.getMaxHeight();
        int myWidth = mLayout.getWidth();
        int myMaxWidth = mLayout.getMaxWidth();

        tileSize = (int) (Math.min(mWidth1, mHeight1) / (WORD_LENGTH + TILE_WIDTH_ADJUST));
//        if (IS_PORTRAIT) {
//            floorPercentage = guideLinePercentage;
//            rightPercentage = 1.0f;
//        } else {
//            floorPercentage = 1.0f;
//            rightPercentage = guideLinePercentage;
//        }
    }

    public void makeTiles() {
        // make all the tiles;
        for (int ix = 0; ix < ROW_COUNT * WORD_LENGTH; ix++) {
            TextView k = new Button(mLayout.getContext());
            mLayout.addView(k);
            k.setId(View.generateViewId());
            k.setTextColor(AppCompatResources.getColorStateList(requireContext(), tileTextColor));
            k.setGravity(Gravity.CENTER);
            tile[ix] = k;
        }
//        Guideline floorGuideline = new Guideline(requireContext());
//        int floorGuidelineId = View.generateViewId();
//        floorGuideline.setId(floorGuidelineId);
//        mLayout.addView(floorGuideline);
//        Guideline rightGuideline = new Guideline(requireContext());
//        int rightGuidelineId = View.generateViewId();
//        mLayout.addView(rightGuideline);

        // make the constraints
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
//        set.create(floorGuidelineId, ConstraintSet.HORIZONTAL_GUIDELINE);
//        set.setGuidelinePercent(floorGuidelineId, guideLinePercentage);
//        set.create(rightGuidelineId, ConstraintSet.VERTICAL_GUIDELINE);
//        set.setGuidelinePercent(rightGuidelineId, rightPercentage);

        for (int ix = 0; ix < tile.length; ix++) {
            TextView k = tile[ix];
            int id = k.getId();

            // set tile widths
            set.constrainWidth(id, tileSize);
            set.constrainHeight(id, tileSize);
            set.constrainMaxHeight(id, tileSize);
            int top, left, right, bottom, topS, leftS, rightS, bottomS, topM, leftM, rightM, bottomM;

            // top & bottom constraints
            if (ix == 0) { // top left
                set.setVerticalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                top = ConstraintSet.PARENT_ID;
                topS = ConstraintSet.TOP;
                topM = TILE_MARGIN;
                bottom = tile[ix + WORD_LENGTH].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = TILE_MARGIN;
            } else if (ix == (ROW_COUNT - 1) * WORD_LENGTH) { // bottom left
                top = tile[ix - WORD_LENGTH].getId();
                topS = ConstraintSet.BOTTOM;
                topM = TILE_MARGIN;
                bottom = ConstraintSet.PARENT_ID;
                bottomS = ConstraintSet.BOTTOM;
                bottomM = TILE_MARGIN;
            } else if (ix % WORD_LENGTH == 0) { // other left edge tiles
                top = tile[ix - WORD_LENGTH].getId();
                topS = ConstraintSet.BOTTOM;
                topM = TILE_MARGIN;
                bottom = tile[ix + WORD_LENGTH].getId();
                bottomS = ConstraintSet.TOP;
                bottomM = TILE_MARGIN;
            } else { // all other tiles align vertically to tile at left
                top = tile[ix - 1].getId();
                topS = ConstraintSet.TOP;
                topM = 0;
                bottom = tile[ix - 1].getId();
                bottomS = ConstraintSet.BOTTOM;
                bottomM = 0;
            }

            // left & right constraints
            if (ix % WORD_LENGTH == 0) { // left edge
                set.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED);
                left = ConstraintSet.PARENT_ID;
                leftS = ConstraintSet.LEFT;
                leftM = TILE_MARGIN;
                right = tile[ix + 1].getId();
                rightS = ConstraintSet.LEFT;
                rightM = TILE_MARGIN;
            } else if (ix % WORD_LENGTH == WORD_LENGTH - 1) { // right edge
                left = tile[ix - 1].getId();
                leftS = ConstraintSet.RIGHT;
                leftM = TILE_MARGIN;
                right = ConstraintSet.PARENT_ID;
                rightS = ConstraintSet.RIGHT;
                rightM = TILE_MARGIN;
            } else { // middle
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

    public void updateTiles(int start) {
        if (tile[0] == null) {
            makeTiles();
        }
        int position = mViewModel.getPosition();
        for (int ix = start; ix < position; ix++) {
            TilePair tp = mViewModel.get(ix);
            setTile(ix, tp.getChar(), tp.getStatusId());
        }
        for (int ix = position; ix < WORD_LENGTH * ROW_COUNT; ix++) {
            setTile(ix, KeyboardFragment.BLANK, TilePair.UNCHECKED);
            tile[ix].setText("" + tileSize);
        }
        mViewModel.getKeyboardSignal().setValue(ROW_CHECKED);
    }

    public void setTile(int ix, char c, int d) {
        tile[ix].setText(String.format("%c", c));
        tile[ix].setBackground(AppCompatResources.getDrawable(requireContext(), d));
        if (d == TilePair.UNCHECKED) {
            rowUpdated = false;
        }
    }

    //  // Theme_AppCompat_DayNight_Dialog
    // AlertDialog_AppCompat
    // Base_ThemeOverlay_AppCompat_Dialog_Alert
    // https://www.javatpoint.com/android-alert-dialog-example
    private void newGameQuery(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.game_over));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.new_game), (dialog, id) -> { newGame(); });
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

    private char getChar(View view) {
        if (view.getTag().toString().charAt(0) == KeyboardFragment.ENTER) {
            return KeyboardFragment.ENTER;
        } else if (view.getTag().toString().charAt(0) == KeyboardFragment.DEL) {
            return KeyboardFragment.DEL;
        } else {
            return ((Button) view).getText().charAt(0);
        }
    }

}