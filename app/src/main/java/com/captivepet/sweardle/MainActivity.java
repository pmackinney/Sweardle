package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.captivepet.sweardle.ui.main.GameFragment;
import com.captivepet.sweardle.ui.main.KeyboardFragment;

public class MainActivity extends AppCompatActivity {
    public Size windowSize;
    GameFragment tile;
    KeyboardFragment keyboard;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);

        View parent = (View) container.getParent();
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int gameSize = keyboard.computeSizes(getDisplayContentHeight());
                keyboard.init();
                tile.init(gameSize);
                tile.resetTiles();
            }
        });

        if (savedInstanceState == null) {
            keyboard = KeyboardFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.keyboard_fragment, keyboard).commitNow();
            tile = GameFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.main_fragment, tile).commitNow();
        }
    }

    // https://gist.github.com/dominicthomas/8257203
    public Point getDisplayContentHeight() {
        final WindowManager windowManager = getWindowManager();
        final Point size = new Point();
        int screenHeight = 0, actionBarHeight = 0;
        if (getActionBar() != null) {
            actionBarHeight = getActionBar().getHeight();
        }
        int contentTop = ((ViewGroup) findViewById(android.R.id.content)).getTop();
        windowManager.getDefaultDisplay().getSize(size);
        size.y -= (contentTop + actionBarHeight);
        return size;
    }
}