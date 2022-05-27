package com.captivepet.sweardle;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageButton;
import com.captivepet.sweardle.ui.main.GameFragment;

public class CustomImageButton extends AppCompatImageButton {
    String text;

    public CustomImageButton(Context context) {
        super(context);
        this.setText(String.valueOf(GameFragment.EMPTY));
    }

    public CustomImageButton(Context context, char c) {
        super(context);
        this.setText(String.valueOf(c));
    }

    public String getText() {
        return text;
    }

    public void setText(String s) {
        text = s;
    }
}
