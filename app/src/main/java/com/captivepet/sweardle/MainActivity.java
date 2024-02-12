package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private int keyboardWidth;
    private int gameboardSize;
    private GameFragment game;
    private KeyboardFragment keyboard;
    private MainFragment main;
    private ViewGroup container;
    private final int GAME = 0;
    private final int KEYBOARD = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            main = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.main, main).commitNow();
            game = GameFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.game, game).commitNow();
            keyboard = KeyboardFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.keyboard, keyboard).commitNow();
        }
    }

    @Override
    protected void onResume() {
        if (game == null) {
            game = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.game);
        }
        if (keyboard == null) {
            keyboard = (KeyboardFragment) getSupportFragmentManager().findFragmentById(R.id.keyboard);
        }
        container = findViewById(R.id.fragment_main);
        View parent = (View) container.getParent();
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                computeFragmentSizes(getDisplayContentHeight());
                assert (keyboardWidth > 0 && gameboardSize > 0);
                game.init(gameboardSize);
                keyboard.init(keyboardWidth);
            }
        });
        super.onResume();
    }

    public void onCharKey(View view) {
        game.processChar(((AppCompatButton) view).getText().charAt(0));
    }

    public void onSpecialKey(View view) {
        game.processChar(view.getTag().toString().charAt(0));
    }

    // https://gist.github.com/dominicthomas/8257203
    public Point getDisplayContentHeight() {
        final WindowManager windowManager = getWindowManager();
        final Point size = new Point();
        int actionBarHeight = (getActionBar() != null) ? getActionBar().getHeight() : 0;
        int contentTop = findViewById(android.R.id.content).getTop();
        windowManager.getDefaultDisplay().getSize(size); // x = width, y = height
        size.y -= (contentTop + actionBarHeight);
        return size;
    }

    public void computeFragmentSizes(Point windowSize) {
        if (windowSize.y >= windowSize.x) { // portrait
            keyboardWidth = windowSize.x;
            gameboardSize = Math.min(windowSize.x, (int) (KeyboardFragment.KEYBOARD_WIDTH_TO_HEIGHT_RATIO * windowSize.y));
        } else { // landscape
            gameboardSize = (int) Math.min(windowSize.x - windowSize.y, windowSize.x / 2f);
            gameboardSize = (int) Math.min(windowSize.y, gameboardSize);
            keyboardWidth = windowSize.x - gameboardSize;
        }
    }
}