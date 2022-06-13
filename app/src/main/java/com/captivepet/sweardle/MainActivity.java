package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;

import com.captivepet.sweardle.ui.main.GameFragment;
import com.captivepet.sweardle.ui.main.KeyboardFragment;

public class MainActivity extends AppCompatActivity {
    public Size windowSize;
    GameFragment game;
    KeyboardFragment keyboard;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            keyboard = KeyboardFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.keyboard_fragment, keyboard).commitNow();
            game = GameFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.game_fragment, game).commitNow();
        }
    }

    @Override
    protected void onResume() {
        GameFragment game = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.game_fragment);
        KeyboardFragment keyboard = (KeyboardFragment) getSupportFragmentManager().findFragmentById(R.id.keyboard_fragment);
        container = findViewById(R.id.container);

        View parent = (View) container.getParent();
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                game.setSize(getDisplayContentHeight());
                int gameSize = keyboard.computeSizes(getDisplayContentHeight());
                keyboard.init();
                game.init(gameSize);
            }
        });
        super.onResume();
    }

    public void onCharKey(View view) {
        game.processChar(((Button) view).getText().toString().charAt(0));
    }

    public void onSpecialKey(View view) {
        game.processChar(view.getTag().toString().charAt(0));
    }

    // https://gist.github.com/dominicthomas/8257203
    public Point getDisplayContentHeight() {
        final WindowManager windowManager = getWindowManager();
        final Point size = new Point();
        int screenHeight = 0, actionBarHeight = 0;
        if (getActionBar() != null) {
            actionBarHeight = getActionBar().getHeight();
        }
        int contentTop = findViewById(android.R.id.content).getTop();
        windowManager.getDefaultDisplay().getSize(size);
        size.y -= (contentTop + actionBarHeight);
        return size;
    }
}