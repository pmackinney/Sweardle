package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.captivepet.sweardle.ui.main.KeyboardFragment;
import com.captivepet.sweardle.ui.main.MainFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public Size windowSize;
    public final static Drawable[] frame = new Drawable[4];
    public final static int ROW_COUNT = 6;
    public final static int WORD_LENGTH = 5;
    MainFragment tiles;
    KeyboardFragment keys;
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
                tiles.init(windowSize);
                keys.init(windowSize);
            }
        });

        if (savedInstanceState == null) {
            tiles = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.main_fragment, tiles).commitNow();
            keys = KeyboardFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.keyboard_fragment, keys).commitNow();
        }
    }
}