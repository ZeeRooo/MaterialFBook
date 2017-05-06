package me.zeerooo.materialfb.Bookmarks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import me.zeerooo.materialfb.R;
import me.zeerooo.materialfb.Ui.CookingAToast;
import me.zeerooo.materialfb.WebView.Helpers;

public class ListAdapter extends ArrayAdapter<Helpers> {
    private ArrayList<Helpers> bookmarks;
    private DatabaseHelper DBHelper;

    private static class ViewHolder {
        AppCompatTextView title;
        AppCompatImageButton delete, share;
    }

    public ListAdapter(Context context, ArrayList<Helpers> bk, DatabaseHelper db) {
        super(context, R.layout.bookmarks_listview, bk);
        this.bookmarks = bk;
        this.DBHelper = db;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Helpers bookmark = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bookmarks_listview, parent, false);
            viewHolder.title = (AppCompatTextView) convertView.findViewById(R.id.bookmark_title);
            viewHolder.delete = (AppCompatImageButton) convertView.findViewById(R.id.delete_bookmark);
            viewHolder.share = (AppCompatImageButton) convertView.findViewById(R.id.share_bookmark);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(bookmark.getTitle());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DBHelper.remove(bookmark.getTitle(), bookmark.getUrl());
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
}