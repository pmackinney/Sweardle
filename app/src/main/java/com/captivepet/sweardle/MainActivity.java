package com.captivepet.sweardle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import com.captivepet.sweardle.ui.main.GameFragment;
import com.captivepet.sweardle.ui.main.KeyboardFragment;

public class MainActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();

    ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.keyboard_fragment, KeyboardFragment.newInstance()).commitNow();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.game_fragment, GameFragment.newInstance()).commitNow();
        }
//        View parent = findViewById(android.R.id.content);
//
//        parent.getViewTreeObserver().addOnGlobalLayoutListener(
//            new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    KeyboardFragment keyboard = KeyboardFragment.newInstance();
//                    GameFragment gameboard = GameFragment.newInstance();
//                    if (savedInstanceState == null) {
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.keyboard_fragment, keyboard).commitNow();
//                        getSupportFragmentManager().beginTransaction().replace(
//                                R.id.main_fragment, gameboard).commitNow();
//                    }
//                    keyboard.setSize(getDisplayContentSize());
//                }
//            });
    }


//    // https://gist.github.com/dominicthomas/8257203
//    public Point getDisplayContentSize() {
//        WindowManager wm = getWindowManager();
//        Point size = new Point();
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            final WindowMetrics metrics = wm.getCurrentWindowMetrics();
//            size.x = metrics.getBounds().width();
//            size.y = metrics.getBounds().height();
//            Log.d(TAG, String.format("size from metrics: %d %d", size.x, size.y));
//        } else {
//            wm.getDefaultDisplay().getSize(size);
//            int actionBarHeight = (getActionBar() != null) ? getActionBar().getHeight() : androidx.appcompat.R.attr.actionBarSize;
//            int statusBarHeight = findViewById(android.R.id.content).getTop() - actionBarHeight;
//            size.y -= actionBarHeight + statusBarHeight;
//            Log.d(TAG, String.format("size from getSize() %d %d", size.x, size.y));
//        }
//        return size;
//    }
}
