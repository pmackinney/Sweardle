package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.captivepet.sweardle.ui.main.GameFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.main_fragment, new GameFragment()).commitNow();
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