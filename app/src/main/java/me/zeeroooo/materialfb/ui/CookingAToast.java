/*
 * Created by ZeeRooo
 * https://github.com/ZeeRooo
 */
package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import me.zeeroooo.materialfb.R;

public class CookingAToast {

    public static @CheckResult
    Toast cooking(Context context, CharSequence message_to_show, int text_color, int background, int icon_toast, boolean duration) {

        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cooking_a_toast, null);
        view.setBackgroundColor(background);

        AppCompatImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(icon_toast);

        AppCompatTextView message = view.findViewById(R.id.message);
        message.setText(message_to_show);
        message.setTextColor(text_color);

        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);

        if (duration) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        return toast;
    }

}