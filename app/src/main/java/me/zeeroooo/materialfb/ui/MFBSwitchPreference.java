package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import me.zeeroooo.materialfb.MFB;

public class MFBSwitchPreference extends SwitchPreference {

    private Switch aSwitch, tempSwitch;
    private View view;

    public MFBSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MFBSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MFBSwitchPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        aSwitch = findSwitchInChildViews((ViewGroup) holder.itemView);

        if (aSwitch != null) {
            changeColor(aSwitch.isChecked());
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeColor(isChecked));
        }
    }

    private Switch findSwitchInChildViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);

            if (view instanceof Switch)
                return (Switch) view;
            else if (view instanceof ViewGroup) {
                tempSwitch = findSwitchInChildViews((ViewGroup) view);
                if (tempSwitch != null)
                    return tempSwitch;
            }
        }

        return null;
    }

    private void changeColor(boolean checked) {
        try {
            aSwitch.getThumbDrawable().setColorFilter(checked ? MFB.colorAccent : Color.parseColor("#ECECEC"), PorterDuff.Mode.SRC_ATOP);
            aSwitch.getTrackDrawable().setColorFilter(checked ? MFB.colorPrimaryDark : Color.parseColor("#B9B9B9"), PorterDuff.Mode.SRC_ATOP);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
