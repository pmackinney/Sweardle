package com.captivepet.sweardle.ui.main;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageButton;
import org.w3c.dom.Text;

public class CustomImageButton extends AppCompatImageButton {
    Text text;

    public CustomImageButton(Context context) {
        super(context);
    }

    public Text getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setTextContent(text);
    }
}
