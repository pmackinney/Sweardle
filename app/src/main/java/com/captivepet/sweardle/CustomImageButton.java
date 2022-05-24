package com.captivepet.sweardle;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.Locale;

public class CustomImageButton extends AppCompatImageButton {
    String text;

    public CustomImageButton(Context context, char c) {
        super(context);
        this.setText(String.format(Locale.US, "%c", c));
    }

    public String getText() {
        return text;
    }

    public void setText(String s) {
        text = s;
    }
}
