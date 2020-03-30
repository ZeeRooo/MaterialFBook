package me.zeeroooo.materialfb.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.misc.DatabaseHelper;
import me.zeeroooo.materialfb.misc.ModelBookmarks;
import me.zeeroooo.materialfb.ui.CookingAToast;

public class AdapterBookmarks extends ArrayAdapter<ModelBookmarks> {
    private ArrayList<ModelBookmarks> bookmarks;
    private DatabaseHelper DBHelper;
    private ModelBookmarks modelBookmarks;
    private ViewHolder viewHolder;
    private LayoutInflater layoutInflater;

    public AdapterBookmarks(Context context, ArrayList<ModelBookmarks> bk, DatabaseHelper db) {
        super(context, R.layout.bookmarks_listview, bk);
        this.bookmarks = bk;
        this.DBHelper = db;
    }

    private static class ViewHolder {
        private TextView title;
        private ImageButton delete, share;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        modelBookmarks = getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.bookmarks_listview, parent, false);
            viewHolder.title = convertView.findViewById(R.id.bookmark_title);
            viewHolder.delete = convertView.findViewById(R.id.delete_bookmark);
            viewHolder.share = convertView.findViewById(R.id.share_bookmark);

            setBackground(viewHolder.share);
            setBackground(viewHolder.delete);
            viewHolder.share.setColorFilter(viewHolder.title.getCurrentTextColor());
            viewHolder.delete.setColorFilter(viewHolder.title.getCurrentTextColor());

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.title.setText(modelBookmarks.getTitle());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DBHelper.remove(modelBookmarks.getTitle(), modelBookmarks.getUrl(), null);
                bookmarks.remove(position);
                notifyDataSetChanged();
                CookingAToast.cooking(getContext(), getContext().getString(R.string.remove_bookmark) + " " + modelBookmarks.getTitle(), Color.WHITE, Color.parseColor("#fcd90f"), R.drawable.ic_delete, false).show();
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, modelBookmarks.getUrl());
                getContext().startActivity(Intent.createChooser(shareIntent, getContext().getString(R.string.context_share_link)));
            }
        });

        return convertView;
    }

    private void setBackground(ImageButton btn) {
        final TypedValue typedValue = new TypedValue();
        int bg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bg = android.R.attr.selectableItemBackgroundBorderless;
        else
            bg = android.R.attr.selectableItemBackground;
        getContext().getTheme().resolveAttribute(bg, typedValue, true);
        btn.setBackgroundResource(typedValue.resourceId);

    }
}