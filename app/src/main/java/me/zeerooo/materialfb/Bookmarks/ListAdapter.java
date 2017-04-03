package me.zeerooo.materialfb.Bookmarks;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import me.zeerooo.materialfb.R;
import me.zeerooo.materialfb.Ui.CookingAToast;
import me.zeerooo.materialfb.WebView.Helpers;

public class ListAdapter extends ArrayAdapter<Helpers> {
    ArrayList<Helpers> bookmarks;
    private DatabaseHelper DBHelper;

    private static class ViewHolder {
        TextView title;
        ImageButton delete;
    }

    public ListAdapter(Context context, ArrayList<Helpers> bk, DatabaseHelper db) {
        super(context, R.layout.bookmarks_listview, bk);
        this.bookmarks = bk;
        this.DBHelper = db;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Helpers bookmark = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bookmarks_listview, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.bookmark_title);
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.delete_bookmark);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(bookmark.getTitle());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Helpers item = getItem(position);
                DBHelper.remove(item.getTitle(), item.getUrl());
                bookmarks.remove(position);
                notifyDataSetChanged();
                CookingAToast.cooking(getContext(), getContext().getString(R.string.remove_bookmark) + " " + item.getTitle(), Color.WHITE, Color.parseColor("#fcd90f"), R.drawable.ic_delete, false).show();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
}