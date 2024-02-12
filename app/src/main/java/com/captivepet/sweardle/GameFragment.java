package com.captivepet.sweardle;

import static com.captivepet.sweardle.R.id.fragment_game;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
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
import android.widget.Toast;

import java.util.List;


public class GameFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

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
    private ViewGroup container;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                this.container = container;
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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    https://stackoverflow.com/questions/20934634/android-menu-item-open-new-fragment
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//
//            case R.id.action_reset:
//
//                Unsafe.uhs1a.setSelection(0);
//                Unsafe.uhs1b.setSelection(0);
//                Unsafe.uhs1c.setSelection(0);
//                Precondition.phs1a.setSelection(0);
//                Precondition.phs1b.setSelection(0);
//                Precondition.phs1c.setSelection(0);
//
//            case R.id.action_about:
//                Fragment newFragment = new TheFragmentYouWantToOpen();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.frame_container, newFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//                //frame_container is the id of the container for the fragment
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
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
            case R.id.menu_expert:
                Fragment newFragment = new ExpertFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_main, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //frame_container is the id of the container for the fragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Redraw the gameboard
     * @param pairList - LiveData object that triggers this method
     */
    public void onChanged(List<TilePair> pairList) {
        if (tile[0] == null) {
            return;
        }
        for (AppCompatTextView tv : tile) {
            int ix = Integer.parseInt(tv.getTag().toString());
            tile[ix].setText(String.format("%c", mViewModel.getTileChar(ix)));
            tile[ix].setBackground(AppCompatResources.getDrawable(requireContext(), mViewModel.getTileStatus(ix)));
        }
    }

    public void processChar(char keyChar) {
        boolean GUESS_TESTED = false;
        int postion = mViewModel.getPosition();
        if (postion > 0
            && postion % GameFragment.WORD_LENGTH == 0
            && mViewModel.getTileStatus(postion - 1) != TilePair.UNCHECKED) {
            GUESS_TESTED = true;
        }
        switch(keyChar) {
        case KeyboardFragment.ENTER:
            List<TilePair> guess = mViewModel.getLastRow(); // valid word or null
            if (guess == null || guess.size() < WORD_LENGTH) {
                return;
            }
            if (!GUESS_TESTED) {
                if (mViewModel.validateGuess()) { // word m`ust be in dict
                    boolean WINNER = mViewModel.testWord();
                    if (WINNER) {
                        newGameQuery(getString(R.string.congratulations));
                    } else if (mViewModel.getRowsDone() == ROW_COUNT) {
                        newGameQuery(String.format(getString(R.string.taunt), mViewModel.getSolution(false)));
                    }
                } else {
                    badWordAlert();
                }
            }
            break;
        case KeyboardFragment.DEL:
            TilePair tp = mViewModel.getLastChar();
            if (tp != null && tp.getStatus() == TilePair.UNCHECKED) {
                Log.d(ContentValues.TAG, String.format("tp = %c, %s", tp.getChar(), TilePair.getStatusName(tp.getStatus())));
                mViewModel.deleteLastChar();
            }
            break;
        default:
            if (KeyboardFragment.firstLetter <= keyChar && keyChar<= KeyboardFragment.lastLetter) {
                if (GUESS_TESTED || mViewModel.getPosition() == 0 || mViewModel.getPosition() % WORD_LENGTH != 0) {
                    mViewModel.addChar(keyChar);
                }
            }
        }
    }

    private AppCompatTextView getTile(int ix) {
        Log.d(ContentValues.TAG, "getDefaultTile");
        AppCompatTextView tv;
        if (tile[ix] != null) {
            tv = tile[ix];
        } else {
            tv = new AppCompatTextView(mLayout.getContext());
            tv.setId(View.generateViewId());
            tv.setTag(String.format("%02d", ix));
        }
            tv.setTextColor(AppCompatResources.getColorStateList(requireContext(), R.color.black));
            return tv;
    }

    public void init(int height) {
        int tileSize = height / (WORD_LENGTH + TILE_WIDTH_ADJUST);
        // make all the tiles;
        for (int ix = 0; ix < ROW_COUNT * WORD_LENGTH; ix++) {
            if (tile[ix] == null) {
                tile[ix] = getTile(ix);
                mLayout.addView(tile[ix]);
            }
        }
        ConstraintSet set = new ConstraintSet();
        set.clone(mLayout);
        for (int ix = 0; ix < tile.length; ix++) {
            AppCompatTextView k = tile[ix];

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
        onChanged(mViewModel.getPairList());
        Log.d(TAG, "init ends");
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