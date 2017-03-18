/*
 * Created by ZeeRooo
 * https://github.com/ZeeRooo
 */
package me.zeerooo.materialfb.Ui;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import me.zeerooo.materialfb.R;

public class CookingAToast {

    public static @CheckResult Toast cooking(Context context, int message_to_show, int text_color, int background, int icon_toast, boolean duration) {

        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cooking_a_toast, null);
        view.setBackgroundColor(background);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(icon_toast);

        TextView message = (TextView) view.findViewById(R.id.message);
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