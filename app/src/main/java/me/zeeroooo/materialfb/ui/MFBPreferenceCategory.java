package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import me.zeeroooo.materialfb.MFB;

public class MFBPreferenceCategory extends PreferenceCategory {

    public MFBPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MFBPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MFBPreferenceCategory(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        ((TextView) holder.findViewById(android.R.id.title)).setTextColor(MFB.colorAccent);
    }
}
