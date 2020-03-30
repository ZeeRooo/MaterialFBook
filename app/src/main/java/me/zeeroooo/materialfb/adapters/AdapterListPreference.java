package me.zeeroooo.materialfb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.preference.ListPreference;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;

public class AdapterListPreference extends ArrayAdapter<CharSequence> {
    private String defaultValue;
    private CharSequence charSequence;
    private ViewHolder viewHolder;
    private LayoutInflater layoutInflater;

    public AdapterListPreference(Context context, ListPreference listPreference) {
        super(context, R.layout.bookmarks_listview, listPreference.getEntries());
        defaultValue = listPreference.getValue();
    }

    public AdapterListPreference(Context context, CharSequence[] charSequences) {
        super(context, R.layout.bookmarks_listview, charSequences);
        defaultValue = "";
    }

    private static class ViewHolder {
        private CheckedTextView checkedTextView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        charSequence = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);

            viewHolder.checkedTextView = convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.checkedTextView.setText(charSequence);

        viewHolder.checkedTextView.setChecked(defaultValue.contentEquals(charSequence));

        viewHolder.checkedTextView.getCheckMarkDrawable().setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(MFB.colorAccent, BlendModeCompat.SRC_ATOP));

        return convertView;
    }
}
