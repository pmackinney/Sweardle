package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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
                windowSize = new Size(container.getMeasuredWidth(), container.getMeasuredHeight());
                int tileSize = keyboard.init(windowSize);
                tile.init(tileSize);
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
}