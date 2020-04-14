/*
 * Code taken from:
 * - FaceSlim by indywidualny. Thanks.
 * - Toffed by JakeLane. Thanks.
 */
package me.zeeroooo.materialfb.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.fragments.SettingsFragment;

public class SettingsActivity extends MFBActivity {

    @Override
    protected void create(Bundle savedInstanceState) {
        super.create(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();

        findViewById(R.id.settings_root_view).setBackgroundColor(MFB.colorPrimary);

        final Toolbar mToolbar = findViewById(R.id.settings_toolbar);
        mToolbar.setTitle(getString(R.string.settings));
        mToolbar.setNavigationIcon(R.mipmap.ic_launcher);

        final Drawable backArrowDrawable = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);

        mToolbar.setTitleTextColor(MFB.textColor);
        backArrowDrawable.setColorFilter(MFB.textColor, PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeAsUpIndicator(backArrowDrawable);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class).putExtra("apply", true));

        super.onBackPressed();
    }
}