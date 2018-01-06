package me.zeeroooo.materialfb.Misc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.Ui.CookingAToast;

public class BlacklistAdapter extends ArrayAdapter<BlackListH> {
    private ArrayList<BlackListH> BlackLH;
    private DatabaseHelper DBHelper;

    private static class ViewHolder {
        TextView title;
        ImageButton delete;
    }

    public BlacklistAdapter(Context context,ArrayList<BlackListH> BlackLH, DatabaseHelper db) {
        super(context, R.layout.blacklist_listview, BlackLH);
        this.DBHelper = db;
        this.BlackLH = BlackLH;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        final BlackListH h = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.blacklist_listview, parent, false);
            viewHolder.title = convertView.findViewById(R.id.blacklist_word);
            viewHolder.delete = convertView.findViewById(R.id.delete_word);

            viewHolder.delete.setColorFilter(viewHolder.title.getCurrentTextColor());

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.title.setText(h.getWord());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DBHelper.remove(null,null, h.getWord());
                BlackLH.remove(position);
                notifyDataSetChanged();
                CookingAToast.cooking(getContext(), getContext().getString(R.string.remove_bookmark) + " " + h.getWord(), Color.WHITE, Color.parseColor("#fcd90f"), R.drawable.ic_delete, false).show();
            }
        });
        return convertView;
    }
}