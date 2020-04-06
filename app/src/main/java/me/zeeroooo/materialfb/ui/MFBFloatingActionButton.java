package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;

public class MFBFloatingActionButton extends LinearLayout {
    private byte id;
    private boolean shown = true;
    private FloatingActionButton floatingActionButton;
    private LayoutParams layoutParams;

    public MFBFloatingActionButton(@NonNull Context context) {
        super(context);
        init();
    }

    public MFBFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MFBFloatingActionButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 16, 16);

        addButton(getResources().getDrawable(R.drawable.ic_fab_menu), "", FloatingActionButton.SIZE_NORMAL, view -> {
            for (id = (byte) (getChildCount() - 1); id > 0; id--) {
                if (shown)
                    ((FloatingActionButton) findViewById(id)).hide();
                else
                    ((FloatingActionButton) findViewById(id)).show();
            }

            shown = !shown;
        });
    }

    public void show() {
        if (!shown) {
            ((FloatingActionButton) findViewById(0)).show();
            shown = true;
        }
    }

    public void hide() {
        if (shown) {
            floatingActionButton = findViewById(0);
            floatingActionButton.performClick();
            floatingActionButton.hide();
        }
    }

    public void addButton(Drawable icon, String description, int size, OnClickListener onClickListener) {
        floatingActionButton = new FloatingActionButton(getContext());
        floatingActionButton.setImageDrawable(icon);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(MFB.colorAccent));
        floatingActionButton.setColorFilter(MFB.colorAccent == Color.WHITE ? MFB.colorPrimary : Color.WHITE);
        floatingActionButton.setOnClickListener(onClickListener);
        floatingActionButton.setSize(size);
        floatingActionButton.setContentDescription(description);
        floatingActionButton.setId(id);

        floatingActionButton.setLayoutParams(layoutParams);

        addView(floatingActionButton, 0);

        id++;
    }
}
