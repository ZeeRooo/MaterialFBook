package me.zeeroooo.materialfb.Misc;

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

import java.util.ArrayList;

import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;

public class BookmarksAdapter extends ArrayAdapter<BookmarksH> {
    private ArrayList<BookmarksH> bookmarks;
    private DatabaseHelper DBHelper;

    private static class ViewHolder {
        TextView title;
        ImageButton delete, share;
    }

    public BookmarksAdapter(Context context, ArrayList<BookmarksH> bk, DatabaseHelper db) {
        super(context, R.layout.bookmarks_listview, bk);
        this.bookmarks = bk;
        this.DBHelper = db;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final BookmarksH bookmark = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bookmarks_listview, parent, false);
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

        viewHolder.title.setText(bookmark.getTitle());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DBHelper.remove(bookmark.getTitle(), bookmark.getUrl(), null);
                bookmarks.remove(position);
                notifyDataSetChanged();
                CookingAToast.cooking(getContext(), getContext().getString(R.string.remove_bookmark) + " " + bookmark.getTitle(), Color.WHITE, Color.parseColor("#fcd90f"), R.drawable.ic_delete, false).show();
            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, bookmark.getUrl());
                getContext().startActivity(Intent.createChooser(shareIntent, getContext().getString(R.string.context_share_link)));
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private void setBackground(View btn) {
        TypedValue typedValue = new TypedValue();
        int bg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bg = android.R.attr.selectableItemBackgroundBorderless;
        else
            bg = android.R.attr.selectableItemBackground;
        getContext().getTheme().resolveAttribute(bg, typedValue, true);
        btn.setBackgroundResource(typedValue.resourceId);
    }
}