package com.captivepet.sweardle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.TextView;

import com.captivepet.sweardle.ui.main.KeyboardFragment;
import com.captivepet.sweardle.ui.main.MainFragment;

import java.util.Locale;

import kotlin.Suppress;

public class MainActivity extends AppCompatActivity {
    Size windowSize;
    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);

        View parent = (View) container.getParent();
        parent.post(new Runnable() {
            public void run() {
                windowSize = new Size(container.getMeasuredWidth(), container.getMeasuredHeight());
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.main_fragment, MainFragment.newInstance()).commitNow();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.keyboard_fragment, KeyboardFragment.newInstance()).commitNow();
        }
    }

    public String getSize() {
        if (windowSize == null || windowSize.getWidth() == 0 || windowSize.getHeight() == 0) {
            windowSize = new Size(container.getMeasuredWidth(), container.getMeasuredHeight());
        }
        return windowSize.toString();
    }

    public void update(View view) {
        ((TextView) view).setText(String.format(Locale.US, "Size: %s", getSize()));
    }
}